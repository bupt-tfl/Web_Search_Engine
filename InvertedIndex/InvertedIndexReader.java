package hw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class InvertedIndexReader {
	
	VariableByteCode variableByteCode = new VariableByteCode();

	private RandomAccessFile randomAccessIndexFile;

	private BufferedReader bufferedLexiconReader;
	
	public void readIndexFile(String lexiconPath, String indexPath) throws IOException{
		randomAccessIndexFile = new RandomAccessFile(indexPath,"r");
		
		File lexiconFile = new File(lexiconPath);
		FileReader lexiconReader = new FileReader(lexiconFile);
		bufferedLexiconReader = new BufferedReader(lexiconReader);
			
		String lexicon = null;
		while((lexicon = bufferedLexiconReader.readLine()) != null){
			String[] val = lexicon.split(":|,");
			String term = val[0].trim();
			long offset = Integer.parseInt(val[1].trim());
			int length = Integer.parseInt(val[2].trim());
			randomAccessIndexFile.seek(offset);
			byte[] compressed = new byte[length];
			randomAccessIndexFile.read(compressed);
			List<Integer> postings = variableByteCode.decode(compressed);
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%s : ", term));
			for(int i = 1; i < postings.size(); i += 2){
				sb.append(String.format("%d,%d ", postings.get(i-1), postings.get(i)));
			}
			System.out.println(sb.toString());
		}
	}
}
