package hw2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class InvertedIndexWriter {

	private FileOutputStream fos;
	
	public void writeIndexFile(Map<String, Map<Integer, Integer>> index, String filePath){
		long time = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, Map<Integer, Integer>>> iter = index.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Map<Integer, Integer>> key = iter.next();
			Map<Integer, Integer> entry = index.get(key.getKey());
			Iterator<Entry<Integer, Integer>> entryIter = entry.entrySet().iterator();
			StringBuilder val = new StringBuilder();
			while(entryIter.hasNext()){
				Entry<Integer, Integer> e = entryIter.next();
				val.append(String.format("%d,%d ", e.getKey(),e.getValue()));
			}
			sb.append(String.format("%s : %s\n", key.getKey(), val.toString()));
		}
		try {
			File file = new File(filePath);
			fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
			System.out.println(String.format("Writetime: %d", System.currentTimeMillis()-time));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
