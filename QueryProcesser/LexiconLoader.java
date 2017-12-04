package hw3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class LexiconLoader {
	
	private BufferedReader br;

	public ArrayList<HashMap<String, String>> loadLexicon(String lexiconPath) throws IOException{
		
		FileReader fr = new FileReader(new File(lexiconPath));
		br = new BufferedReader(fr);
		ArrayList<HashMap<String, String>> termLists = new ArrayList<>();
		HashMap<String,String> temp = new HashMap<>();
		String line = null;
		
		while((line = br.readLine()) != null){
			if(temp.size() > 786430){
				termLists.add(temp);
				temp = new HashMap<>();
			}
			if(line.substring(0, line.indexOf(": ")).length() < 10)
				temp.put(line.substring(0, line.indexOf(": ")), line.substring(line.indexOf(": ") + 2));
		}
		termLists.add(temp);
		return termLists;
	}
}
