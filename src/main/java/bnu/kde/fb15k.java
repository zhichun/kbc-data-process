package bnu.kde;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

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
                if(ss[1].contains("common.identity.daylife_topic")||ss[1].contains("type.object.key")||ss[1].contains("wikipedia")||ss[1].contains("_id>")||ss[2].startsWith("<http")||ss[1].startsWith("<http://rdf.freebase.com/key/"))continue;
                bufferedWriter.write(line);
                bufferedWriter.newLine();

            }
        } finally {
            LineIterator.closeQuietly(it);
            bufferedWriter.close();
        }
        bufferedWriter.close();
    }


    public static void main( String[] args )
    {
        try {
            filter("/home/zcwang/Data/Freebase/FB15k/fb15k_all_literal_facts.txt","/home/zcwang/Data/Freebase/FB15k/fb15k_all_literal_facts_cleaned_1.txt");
            //getLiteralFactsForFB15K("/home/zcwang/Data/Freebase/FB15k/fb15k_all_relational_facts.txt",
            // "/home/zcwang//Data/Freebase/freebase-rdf-latest","/home/zcwang/Data/Freebase/FB15k/fb15k_all_literal_facts.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
