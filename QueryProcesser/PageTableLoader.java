package hw3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PageTableLoader {
	
	private BufferedReader br;

	public ArrayList<ArrayList<String>> load(String file) throws IOException{
		FileReader fr = new FileReader(new File(file));
		br = new BufferedReader(fr);
		ArrayList<ArrayList<String>> pageTable = new ArrayList<>();
		ArrayList<String> temp = new ArrayList<>(500000);
		
		String line = null;
		while((line = br.readLine()) != null){
			if(temp.size() >= 500000){
				pageTable.add(temp);
				temp = new ArrayList<>(500000);
			}
			temp.add(line.substring(line.indexOf(": ") + 2));
		}
		pageTable.add(temp);
		return pageTable;
	}

	
}
