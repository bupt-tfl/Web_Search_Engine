package hw3;

import java.util.HashMap;

public class IndexList {
	private HashMap<String, Index> indexList ;
	
	public IndexList(){
		this.indexList = new HashMap<>();
	}
	
	public void addIndex(String term, Index index){
		indexList.put(term, index);
	}
	
	public Index openList(String term){
		if(!indexList.containsKey(term)) System.out.println(String.format("%s not found", term));
		return indexList.get(term);
	}
	
	public String[] getTerms(){
		String[] terms = new String[indexList.keySet().size()];
		terms = indexList.keySet().toArray(terms);
		return terms;
	}
	
	public int nextGEQ(String term, int k){
		return indexList.get(term).nextGEQ(k);
	}
	
	public int getFreq(String term){
		return indexList.get(term).getFreq();
	}
}
