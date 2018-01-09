package bnu.kde;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Kgstore {

	public static final int relationalFacts = 0;
	public static final int literalFacts = 1;
	
	public static final int sub2pred2obj = 0;
	public static final int sub2obj2pred = 1;
	public static final int pred2obj2sub = 2;
	public static final int pred2sub2obj = 3;
	public static final int obj2pred2sub = 4;
	public static final int obj2sub2pred = 5;
	public static final int sub2pred2lit = 6;
	public static final int pred2sub2lit = 7;
	public static final int allIndex=8;

	protected Set<String> allentities = new HashSet<String>();
	protected Set<String> allrelations= new HashSet<String>();
	protected Set<String> allattributes= new HashSet<String>();
	
	protected Map<String, Map<String, Set<String> > > subject2predicate2object=null;
	protected Map<String, Map<String, Set<String> > > subject2object2predicate=null;
	protected Map<String, Map<String, Set<String> > > predicate2object2subject=null;
	protected Map<String, Map<String, Set<String> > > predicate2subject2object=null;
	protected Map<String, Map<String, Set<String> > > object2predicate2subject=null;
	protected Map<String, Map<String, Set<String> > > object2subject2predicate=null;
	
	protected Map<String, Map<String, Set<String> > > subject2predicate2literal=null;
	protected Map<String, Map<String, Set<String> > > predicate2subject2literal=null;

	public Kgstore(){
			subject2predicate2object = new HashMap<String, Map<String, Set<String>>>();
			subject2object2predicate = new HashMap<String, Map<String, Set<String>>>();
			predicate2object2subject = new HashMap<String, Map<String, Set<String>>>();
			predicate2subject2object = new HashMap<String, Map<String, Set<String>>>();
			object2predicate2subject = new HashMap<String, Map<String, Set<String>>>();
			object2subject2predicate = new HashMap<String, Map<String, Set<String>>>();
			subject2predicate2literal = new HashMap<String, Map<String, Set<String>>>();
			predicate2subject2literal = new HashMap<String, Map<String, Set<String>>>();
	}

	public Kgstore(String mode){
		String [] modes = mode.split(" ");
		for (int i=0;i<modes.length;i++) {
			if (modes[i].equals("spo")) subject2predicate2object = new HashMap<String, Map<String, Set<String>>>();
			if (modes[i].equals("sop")) subject2object2predicate = new HashMap<String, Map<String, Set<String>>>();
			if (modes[i].equals("pso")) predicate2object2subject = new HashMap<String, Map<String, Set<String>>>();
			if (modes[i].equals("pos")) predicate2subject2object = new HashMap<String, Map<String, Set<String>>>();
			if (modes[i].equals("ops")) object2predicate2subject = new HashMap<String, Map<String, Set<String>>>();
			if (modes[i].equals("osp")) object2subject2predicate = new HashMap<String, Map<String, Set<String>>>();
			if (modes[i].equals("spl")) subject2predicate2literal = new HashMap<String, Map<String, Set<String>>>();
			if (modes[i].equals("psl")) predicate2subject2literal = new HashMap<String, Map<String, Set<String>>>();
		}
	}
	public Kgstore(int [] indexcodes){

			if(indexcodes.length!=8){
				System.err.println("Length of indexcodes should be 8!");
			}
			if(indexcodes[sub2pred2obj]!=0)subject2predicate2object=new HashMap<String, Map<String, Set<String> > >();
			if(indexcodes[sub2obj2pred]!=0)subject2object2predicate=new HashMap<String, Map<String, Set<String> > >();
			if(indexcodes[pred2obj2sub]!=0)predicate2object2subject=new HashMap<String, Map<String, Set<String> > >();
			if(indexcodes[pred2sub2obj]!=0)predicate2subject2object=new HashMap<String, Map<String, Set<String> > >();
			if(indexcodes[obj2pred2sub]!=0)object2predicate2subject=new HashMap<String, Map<String, Set<String> > >();
			if(indexcodes[obj2sub2pred]!=0)object2subject2predicate=new HashMap<String, Map<String, Set<String> > >();
			if(indexcodes[sub2pred2lit]!=0)subject2predicate2literal=new HashMap<String, Map<String, Set<String> > >();
			if(indexcodes[pred2sub2lit]!=0)predicate2subject2literal=new HashMap<String, Map<String, Set<String> > >();

	}

    public Kgstore(int model, int [] indexcodes){
    	if(model==allIndex){
    		subject2predicate2object=new HashMap<String, Map<String, Set<String> > >();
    		subject2object2predicate=new HashMap<String, Map<String, Set<String> > >();
    		predicate2object2subject=new HashMap<String, Map<String, Set<String> > >();
    		predicate2subject2object=new HashMap<String, Map<String, Set<String> > >();
    		object2predicate2subject=new HashMap<String, Map<String, Set<String> > >();
    		object2subject2predicate=new HashMap<String, Map<String, Set<String> > >();
    		subject2predicate2literal=new HashMap<String, Map<String, Set<String> > >();
    		predicate2subject2literal=new HashMap<String, Map<String, Set<String> > >();
    	}else{
    		if(indexcodes.length!=8){
    			System.err.println("Length of indexcodes should be 8!");
    		}
    		if(indexcodes[sub2pred2obj]!=0)subject2predicate2object=new HashMap<String, Map<String, Set<String> > >();
    		if(indexcodes[sub2obj2pred]!=0)subject2object2predicate=new HashMap<String, Map<String, Set<String> > >();
    		if(indexcodes[pred2obj2sub]!=0)predicate2object2subject=new HashMap<String, Map<String, Set<String> > >();
    		if(indexcodes[pred2sub2obj]!=0)predicate2subject2object=new HashMap<String, Map<String, Set<String> > >();
    		if(indexcodes[obj2pred2sub]!=0)object2predicate2subject=new HashMap<String, Map<String, Set<String> > >();
    		if(indexcodes[obj2sub2pred]!=0)object2subject2predicate=new HashMap<String, Map<String, Set<String> > >();
    		if(indexcodes[sub2pred2lit]!=0)subject2predicate2literal=new HashMap<String, Map<String, Set<String> > >();
    		if(indexcodes[pred2sub2lit]!=0)predicate2subject2literal=new HashMap<String, Map<String, Set<String> > >();
    	}
    }
    
    private void addTriple(String s,String p,String o){
    	allentities.add(s);
    	allentities.add(o);
    	allrelations.add(p);
    	this.insertData(this.subject2predicate2object, s, p, o);
    	this.insertData(this.subject2object2predicate, s, o, p);
    	this.insertData(this.object2predicate2subject, o, p, s);
    	this.insertData(this.object2subject2predicate, o, s, p);
    	this.insertData(this.predicate2subject2object, p, s, o);
    	this.insertData(this.predicate2object2subject, p, o, s);
    }
    
    private void addLiteralTriple(String s, String p, String lit){
    	allentities.add(s);
    	allattributes.add(p);
    	this.insertData(subject2predicate2literal, s, p, lit);
    	this.insertData(predicate2subject2literal, p, s, lit);
    }
    
    private void insertData(Map<String, Map<String, Set<String> > > index, String key1,String key2,String value){
    	if(index==null)return;
    	synchronized(index){
    		Map<String, Set<String> > subindex=index.get(key1);
    		if(subindex==null) index.put(key1, subindex=new HashMap<String, Set<String> >());
    		Set<String> values=subindex.get(key2);
    		if(values==null) subindex.put(key2, values=new HashSet<String>());
    		values.add(value);
    	}
    }

    
    private int addURI(String uri,Map<String,Integer> m){
    	synchronized(m){
    		Integer id=m.get(uri);
    		if(id==null){
    			id=m.size()+1;
    			m.put(uri, id);
    		}
    		return id;
    	}
    }
    
    public void load(String path, int type) throws IOException {
    	System.out.println("Loading kg data");
    	long time = System.currentTimeMillis();
    	File in=new File(path);
		LineIterator it = FileUtils.lineIterator(in, "UTF-8");
    	String line=null;
    	int triplecount=0;
    	while (it.hasNext()) {
    		line=it.nextLine();
    		if(line.endsWith(".")){
    			line=StringUtils.substringBeforeLast(line, ".");
    		}
    		String [] ss=line.split("\t");
    		if(ss.length==3){
    			ss[0]=ss[0].trim();
    			ss[1]=ss[1].trim();
    			ss[2]=ss[2].trim();
    			if(type==relationalFacts){
        			this.addTriple(ss[0], ss[1], ss[2]);
    			}else if (type == literalFacts){
    				this.addLiteralTriple(ss[0], ss[1], ss[2]);
    			}
    			
    			triplecount++;
    		}
    	}
    	long last=System.currentTimeMillis()-time;
        String duration=DurationFormatUtils.formatDurationHMS(last);
    	System.out.println(triplecount+" triples are loaded in "+duration);
    	
    	//post processing

    }

    
    public Map<String, Map<String, Set<String> > > getIndex(int indexId){
		switch(indexId)
		{
			case Kgstore.sub2pred2obj:return Collections.unmodifiableMap(this.subject2predicate2object);
			
			case Kgstore.sub2obj2pred:return Collections.unmodifiableMap(this.subject2object2predicate);
			
			case Kgstore.pred2sub2obj:return Collections.unmodifiableMap(this.predicate2subject2object);
			
			case Kgstore.pred2obj2sub:return Collections.unmodifiableMap(this.predicate2object2subject);
			
			case Kgstore.obj2sub2pred:return Collections.unmodifiableMap(this.object2subject2predicate);
			
			case Kgstore.obj2pred2sub:return Collections.unmodifiableMap(this.object2predicate2subject);
			
			case Kgstore.sub2pred2lit:return Collections.unmodifiableMap(this.subject2predicate2literal);
			
			case Kgstore.pred2sub2lit:return Collections.unmodifiableMap(this.predicate2subject2literal);
			
			default: return null;
		}
	}
	
	public Map<String, Set<String> > getSubIndex(String key, int indexId){
		Map<String, Map<String, Set<String> > > index=this.getIndex(indexId);
		if(index==null)return null;
		Map<String, Set<String> > result=index.get(key);
		if(result==null)return null;
		return Collections.unmodifiableMap(result);
	}
	
	public Set<String> getIndexValues(String key1, String key2, int indexId){
		Map<String, Set<String> > subindex=this.getSubIndex(key1, indexId);
		if(subindex==null)return null;
		Set<String> values=subindex.get(key2);
		if(values==null)return null;
		return Collections.unmodifiableSet(values);
	}

	public Map<String,Integer> generateEntityNameIMaps(){
		Map<String,Integer> result = new HashMap<String, Integer>();
		int id=1;
		for (String s:allentities){
			result.put(s,id);
			id++;
		}
		return result;
	}

	public Map<String,Integer> generateRelationNameIMaps(){
		Map<String,Integer> result = new HashMap<String, Integer>();
		int id=1;
		for (String s:allrelations){
			result.put(s,id);
			id++;
		}
		return result;
	}

	public Map<String,Integer> generateAttributeNameIMaps(){
		Map<String,Integer> result = new HashMap<String, Integer>();
		int id=1;
		for (String s:allattributes){
			result.put(s,id);
			id++;
		}
		return result;
	}

	public Set<String> getAllentities() {
		return allentities;
	}

	public Set<String> getAllrelations() {
		return allrelations;
	}

	public Set<String> getAllattributes() {
		return allattributes;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
	}

}
