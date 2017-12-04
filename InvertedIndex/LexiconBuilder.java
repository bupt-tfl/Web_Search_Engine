package hw2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexiconBuilder {
	

	private BufferedReader bufferedReader;
	private DataOutputStream dos;
	VariableByteCode variableByteCode = new VariableByteCode();
	private FileOutputStream lexiconOutput;
	
	public void buildLexicon(String filePath, String indexFile) throws IOException{
		File index = new File(filePath + indexFile);
		FileReader indexReader= new FileReader(index);
		bufferedReader = new BufferedReader(indexReader);
		
		String posting = null;
		
		File finalIndex = new File(filePath + "finalIndex.txt");
		FileOutputStream finalIndexOutputStream = new FileOutputStream(finalIndex);
		dos = new DataOutputStream(finalIndexOutputStream);
		File lexicon = new File(filePath + "lexicon.txt");
		lexiconOutput = new FileOutputStream(lexicon);
		
		long m = 0;
		while((posting = bufferedReader.readLine()) != null){
			String[] val = posting.split(" : ");
			if(val[0].length() > 15) continue;
			String[] postings = val[1].split("  |,| ");
			HashMap<Integer, Integer> map = new HashMap<>();
			for(int i = 1 ; i < postings.length; i += 2){
				map.put(Integer.parseInt(postings[i-1].trim()), Integer.parseInt(postings[i].trim()));
			}
			List<Integer> list = sort(map);
			byte[] compressed = variableByteCode.encode(list);
			dos.write(compressed);
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%s: %d,%d\n", val[0].toLowerCase(), m, compressed.length));
			lexiconOutput.write(sb.toString().getBytes());
			m += compressed.length;
		}
	}
	
	public ArrayList<Integer> sort(HashMap<Integer, Integer> map){
		ArrayList<Integer> list = new ArrayList<>();
		ArrayList<Integer> docs = new ArrayList<>();
		docs.addAll(map.keySet());
		Collections.sort(docs,new Comparator<Integer>(){
			public int compare(Integer a, Integer b){
				return a.compareTo(b);
			}
		});
		int lastDoc = 0;
		for(int i = 0; i < docs.size(); i ++){
			int doc = docs.get(i);
			list.add(doc - lastDoc);
			list.add(map.get(doc));
			lastDoc = doc;
		}
		return list;
	}
}
