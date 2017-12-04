package hw3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hw2.VariableByteCode;

public class Index {

	private String term;

	private VariableByteCode variableByteCode = new VariableByteCode();

	private Map<Integer, Integer> index = new HashMap<>();

	private int position = 0;

	private ArrayList<Integer> docs = new ArrayList<>();

	public Index(String term, byte[] compressedIndex){
		this.setTerm(term);
		List<Integer> indexList = variableByteCode.decode(compressedIndex);
		int lastId = 0;
		for(int i = 1; i < indexList.size(); i += 2){
			int docId = indexList.get(i-1) + lastId;
			index.put(docId, indexList.get(i));
			docs.add(docId);
			lastId = docId;
		}
		docs.sort(new Comparator<Integer>(){
			@Override
			public int compare(Integer a, Integer b){
				return a.compareTo(b);
			}
		});

	}

	public int getFreq(){
		return this.index.get(this.position);
	}
	
	public int getFreq(int docId){
		return this.index.get(docId);
	}

	public int nextGEQ(int k){
		for(int docId : this.docs){
			if(docId >= k){
				this.position = docId;
				return docId;
			}
		}
		return Integer.MAX_VALUE;
	}

	public int size(){
		return this.index.size();
	}

	public boolean contains(int docId){
		return this.index.containsKey(docId);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
