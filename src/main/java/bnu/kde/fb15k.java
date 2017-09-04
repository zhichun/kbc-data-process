package bnu.kde;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class fb15k
{
    public static void getLiteralFactsForFB15K(String fb15kpath,String fbpath,String literalpath) throws IOException {
        //read all entities
        File file = new File(fb15kpath);
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        Set<String> entities = new HashSet<String>();
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                String [] ss = line.split("\t");
                entities.add(ss[0]);
                entities.add(ss[2]);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(literalpath,false),"UTF-8"));
        //get literal facts from fb
        File file2 = new File(fbpath);
        LineIterator it2 = FileUtils.lineIterator(file2, "UTF-8");
        //int count=0;
        try {
            while (it2.hasNext()) {
                //System.out.println(count++);
                String line = it2.nextLine();
                String [] ss = line.split("\t");
                if (ss[2].startsWith("<http://"))continue;
                String e = StringUtils.substringAfter(ss[0],"<http://rdf.freebase.com/ns/");
                e=StringUtils.substringBefore(e,">");
                e=e.replaceAll("\\.", "/");
                e="/"+e;
                if (!entities.contains(e)) continue;
                if (ss[2].contains("@"))continue;

                //String r = StringUtils.substringAfter( ss[1],"<http://rdf.freebase.com/ns/");
                //r=r.replaceAll("\\.","/");
                //r="/"+r;
                System.out.println(e+"\t"+ss[1]+"\t"+ss[2]);
                bufferedWriter.write(e+"\t"+ss[1]+"\t"+ss[2]);
                bufferedWriter.newLine();

            }
        } finally {
            LineIterator.closeQuietly(it);
            bufferedWriter.close();
        }
        bufferedWriter.close();
    }

    public static void filter(String input,String output) throws IOException {
        File file = new File(input);
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output,false),"UTF-8"));

        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                String [] ss = line.split("\t");
                if(ss[1].contains("common.identity.daylife_topic")||
                        ss[1].contains("type.object.key")||
                        ss[1].contains("wikipedia")||
                        ss[1].contains("_id>")||
                        ss[2].startsWith("<http")||
                        ss[1].startsWith("<http://rdf.freebase.com/key/"))continue;
                bufferedWriter.write(line);
                bufferedWriter.newLine();

            }
        } finally {
            LineIterator.closeQuietly(it);
            bufferedWriter.close();
        }
        bufferedWriter.close();
    }

    public static float dateConverter(String date) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        String inputString1 = "1000-01-01";
        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(date);
            long diff = date2.getTime() - date1.getTime();
            float days =  TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return days;
        } catch (ParseException e) {
            System.out.println(date);
            e.printStackTrace();
        }
        return 0;
    }


    public static void generateEvaluationData(String input,String output,String output2) throws IOException {
        int [] kgmode = new int[Kgstore.allIndex];
        kgmode[Kgstore.sub2pred2lit]=1;
        kgmode[Kgstore.pred2sub2lit]=1;
        Kgstore kg = new Kgstore(0,kgmode);
        kg.load(input,Kgstore.literalFacts);

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output,false),"UTF-8"));


        Map<String,Map<String,Set<String> > > spl = kg.getIndex(Kgstore.sub2pred2lit);

        Map<String,Integer> locationCodes = new HashMap<String, Integer>();


        for (String sub : spl.keySet()){
            Map<String,Set<String> > pl = spl.get(sub);
            for (String pred: pl.keySet()){
                if (!pred.contains("<http://rdf.freebase.com/ns/"))continue;
                String newPred = StringUtils.substringAfter(pred,"<http://rdf.freebase.com/ns/");
                newPred = StringUtils.substringBefore(newPred,">");

                if ((pred.contains("_code>") && pred.contains("location")) || (pred.contains("location")&& pred.contains(".iso"))){
                    for (String lit : pl.get(pred)){
                        String v = lit.substring(1,3);
                        Integer code = locationCodes.get(v);
                        if (code == null){
                            code = locationCodes.size();
                            locationCodes.put(v,code);
                        }
                        bufferedWriter.write(sub+"\t"+newPred+"\t"+code);
                        bufferedWriter.newLine();
                        break;
                    }
                    continue;
                }

                for (String lit : pl.get(pred)){
                    System.out.println(lit);
                    if (lit.contains("<http://www.w3.org/2001/XMLSchema#date>")){
                        //String temp = StringUtils.substringAfter(lit,"\"");
                        String temp = StringUtils.removeAll(lit,"\"");
                        temp = StringUtils.substringBefore(temp,"^^");
                        if(temp.equals(""))continue;
                        float days = dateConverter(temp);
                        bufferedWriter.write(sub+"\t"+newPred+"\t"+days);
                        bufferedWriter.newLine();
                        continue;
                    }else if (lit.contains("<http://www.w3.org/2001/XMLSchema#dateTime>")){
                        String temp = StringUtils.removeAll(lit,"\"");
                        temp = StringUtils.substringBefore(temp,"T");
                        if(temp.equals(""))continue;
                        float days = dateConverter(temp);
                        bufferedWriter.write(sub+"\t"+newPred+"\t"+days);
                        bufferedWriter.newLine();
                        continue;
                    }else if (lit.contains("<http://www.w3.org/2001/XMLSchema#gYear>")){
                        String temp = StringUtils.removeAll(lit,"\"");
                        temp = StringUtils.substringBefore(temp,"^^");
                        if(temp.equals(""))continue;
                        temp = temp +"-01-01";
                        float days = dateConverter(temp);
                        bufferedWriter.write(sub+"\t"+newPred+"\t"+days);
                        bufferedWriter.newLine();
                        continue;
                    }else if (lit.equals("true")){
                        bufferedWriter.write(sub+"\t"+newPred+"\t"+1);
                        bufferedWriter.newLine();
                        continue;

                    }else if(lit.equals("false")){
                        bufferedWriter.write(sub+"\t"+newPred+"\t"+0);
                        bufferedWriter.newLine();
                        continue;
                    }else{
                        String temp = StringUtils.removeAll(lit,"\"");
                        if (isNumeric(temp)){
                            bufferedWriter.write(sub+"\t"+newPred+"\t"+temp);
                            bufferedWriter.newLine();
                            continue;
                        }
                    }
                }
            }
        }

        bufferedWriter.close();

        BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output2,false),"UTF-8"));
        for (String k: locationCodes.keySet()){
            bufferedWriter2.write(k+"\t"+locationCodes.get(k));
            bufferedWriter2.newLine();
        }
        bufferedWriter2.close();

    }

    public static void selectTestRelations(String relationalFacts,String candidateRelation) throws IOException {
        File file = new File(candidateRelation);
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        List<String> relations = new ArrayList<String>();
        while (it.hasNext()) {
            relations.add(it.nextLine());
        }
        it.close();

        int [] kgmode = new int[Kgstore.allIndex];

        Kgstore kg = new Kgstore(Kgstore.allIndex,kgmode);
        kg.load(relationalFacts,Kgstore.relationalFacts);

        List<Pair<String,Float>> plits = new ArrayList<Pair<String, Float>>();

        for (String r: relations){
            Map<String,Set<String> > temp = kg.getIndex(Kgstore.pred2sub2obj).get(r);
            float count = 0;
            for (Set<String> ss : temp.values()){
                count+=ss.size();
            }
            plits.add(new Pair<String, Float>(r,count));
        }


        Collections.sort(plits);

        for (Pair p : plits){
            System.out.println(p.getFirst()+"\t"+p.getSecond());
        }

    }

    public static void statistics(String litInput,String relationInput) throws IOException {
        int [] kgmode = new int[Kgstore.allIndex];
        kgmode[Kgstore.sub2pred2lit]=1;
        kgmode[Kgstore.pred2sub2lit]=1;
        kgmode[Kgstore.sub2pred2obj]=1;

        Kgstore kg = new Kgstore(Kgstore.allIndex,kgmode);
        kg.load(litInput,Kgstore.literalFacts);
        kg.load(relationInput,Kgstore.relationalFacts);


        Map<String,Map<String,Set<String> > > spl = kg.getIndex(Kgstore.sub2pred2lit);
        Map<String,Map<String,Set<String> > > psl = kg.getIndex(Kgstore.pred2sub2lit);

        System.out.println("Number of Entities have literal values: "+spl.size());
        System.out.println("Number of Attributes: "+psl.size());

        //get relations with sufficient number of literal facts

        List<Pair<String,Float>> plits = new ArrayList<Pair<String, Float>>();

        for (String pred : kg.getIndex(Kgstore.pred2sub2obj).keySet()){
            Set<String> entities = new HashSet<String>();
            for (String sub : kg.getIndex(Kgstore.pred2sub2obj).get(pred).keySet()){
                entities.add(sub);
                entities.addAll(kg.getIndex(Kgstore.pred2sub2obj).get(pred).get(sub));
            }
            int count=0;
            for (String entity:entities){
                Map<String,Set<String> > temp = spl.get(entity);
                if (temp==null)continue;
                count+=temp.size();
            }
            float avg;// = (float)count/(float)entities.size();
            if(entities.size()==0)avg=0;
            else avg = (float)count/(float)entities.size();
            Pair<String,Float> p=new Pair<String, Float>(pred,avg);
            plits.add(p);
        }

        Collections.sort(plits);

        for (Pair p : plits){
            System.out.println(p.getFirst()+"\t"+p.getSecond());
        }
    }


    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static void generatePRADataFB15K(String trainpath, String validpath, String testpath, String literalfactspath, String testrelationspath, String output) throws IOException {
        //load triples
        Kgstore kgall = new Kgstore("spo spl");
        kgall.load(trainpath,Kgstore.relationalFacts);
        kgall.load(validpath,Kgstore.relationalFacts);
        kgall.load(testpath,Kgstore.relationalFacts);
        kgall.load(literalfactspath,Kgstore.literalFacts);

        Map<String,Integer> entityIds = kgall.generateEntityNameIMaps();
        Map<String,Integer> relationIds= kgall.generateRelationNameIMaps();

        Kgstore kgtrain = new Kgstore("pso");
        kgtrain.load(trainpath,Kgstore.relationalFacts);
        Kgstore kgtest = new Kgstore("pso");
        kgtest.load(testpath,Kgstore.relationalFacts);

        //load relation_list
        List<String> testRelations = IOUtils.readList(testrelationspath);

        //generate output
        for (String tr : testRelations){
            //train

        }

    }





    public static void main( String[] args ) throws IOException {
        //statistics("data/fb15k_all_literal_facts_numeric.txt","data/fb15k_all_relational_facts.txt");
        selectTestRelations("data/fb15k_all_relational_facts.txt","data/test_relations_avg_lit_facts_3.txt");

//        try {
//            generateEvaluationData("data/fb15k_all_literal_facts_cleaned_1.txt",
//                    "data/fb15k_all_literal_facts_numeric.txt",
//                    "data/fb15k_all_literal_facts_locationcodes.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {
//
//
//
//            //dateConverter("1000-01-30");
//
//            //filter("/home/zcwang/Data/Freebase/FB15k/fb15k_all_literal_facts.txt","/home/zcwang/Data/Freebase/FB15k/fb15k_all_literal_facts_cleaned_1.txt");
//            //getLiteralFactsForFB15K("/home/zcwang/Data/Freebase/FB15k/fb15k_all_relational_facts.txt",
//            // "/home/zcwang//Data/Freebase/freebase-rdf-latest","/home/zcwang/Data/Freebase/FB15k/fb15k_all_literal_facts.txt");
////        } catch (IOException e) {
////            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }
}
