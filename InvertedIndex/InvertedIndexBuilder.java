package hw2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReaderFactory;;

public class InvertedIndexBuilder {
	
	private Map<String, Map<Integer,Integer>> invertedIndex = new HashMap<>(); // term to index
	
	private Map<Integer, Integer> index = new HashMap<>(); // docId to count
	
	private Map<Integer, String> doc = new HashMap<>();	// page table
	
	private static int docId = 0;
	
	private static int indexId = 0;
	
	private static int pagetableId = 0;
	
	public void buildIndex(String dataPath, String indexPath, String... files) throws IOException{
		for(String file : files){
			System.out.println(file);
			FileInputStream is = new FileInputStream(dataPath + file);
			ArchiveReader ar = WARCReaderFactory.get(file, is, true);
			
			Iterator<ArchiveRecord> iter = ar.iterator();
			while(iter.hasNext()){
				ArchiveRecord record = iter.next();
				String url = record.getHeader().getUrl();
				long len = record.getHeader().getLength();
				doc.put(docId, String.format("%s ,%d", url, len));	
				/*
				byte[] rawData = IOUtils.toByteArray(record, record.available());
				String content = new String(rawData);
				String[] words = content.split(" |	|\\\\|\\\"|\\'|\\.|\\(|\\)|\\*|\\||\\&|\\^|\\%|$|#|@|!|>|<|,|~|\\+|-|=|/|\n|:|¡±|¡°|£º|£¡|£¿|\\?|\\{|\\}");
				for(String term : words){
					term = term.trim();
					if(term == " ") continue;
					if(!term.matches("^[a-z0-9A-Z]*")) continue;
					if(term.matches("^[0-9]*")) continue;
					index = invertedIndex.getOrDefault(term, new HashMap<>());
					int count = index.getOrDefault(docId, 0);
					index.put(docId, count + 1);
					invertedIndex.put(term, index);
				}
				docId ++;
				if(invertedIndex.size() > 50000){
					InvertedIndexWriter lexiconWriter = new InvertedIndexWriter();
					lexiconWriter.writeIndexFile(invertedIndex, indexPath + String.valueOf(indexId) + ".txt");
					indexId ++;
					invertedIndex.clear();
				}
				*/
				docId ++;
				if(doc.size() > 500000){
					writePageTable(indexPath);
					doc.clear();
				}
			}
		}
		/*
		if(!invertedIndex.isEmpty()){
			InvertedIndexWriter lexiconWriter = new InvertedIndexWriter();
			lexiconWriter.writeIndexFile(invertedIndex, indexPath + String.valueOf(indexId) + ".txt");
		}
		*/
		writePageTable(indexPath);
	}
	
	public void writePageTable(String path){
		Iterator<Map.Entry<Integer, String>> iter = doc.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		while(iter.hasNext()){
			Map.Entry<Integer, String> entry =  iter.next();
			sb.append(String.format("%d: %s\n", entry.getKey(), entry.getValue()));
		}
		File file = new File(String.format("%spageTable%d.txt", path, pagetableId));
		pagetableId ++;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}