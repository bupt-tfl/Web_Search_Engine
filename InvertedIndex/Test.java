package hw2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hw2.InvertedIndexBuilder;
import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.IntegratedIntCompressor;
public class Test {
	
	private static final String DATA_PATH = "D:\\fall2017\\web search engine\\hw2\\inverted_index\\data\\";
	
	private static final String INDEX_PATH = "D:\\fall2017\\web search engine\\hw2\\inverted_index\\data\\index_files\\";

	private static FileOutputStream fos;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		


		String[] file = new String[81];
		for(int i = 0; i < 10; i++){
			file[i] = String.format("CC-MAIN-20170919112242-20170919132242-0000%d.warc.wet.gz", i);
		}

		for(int i = 10; i < 81; i++){
			file[i] = String.format("CC-MAIN-20170919112242-20170919132242-000%d.warc.wet.gz", i);
		}

		InvertedIndexBuilder ib = new InvertedIndexBuilder();
		ib.buildIndex(DATA_PATH, INDEX_PATH, file);

//		LexiconBuilder lb = new LexiconBuilder();
//		lb.buildLexicon(INDEX_PATH, "index.txt");
		
//		InvertedIndexReader ir = new InvertedIndexReader();
//		ir.readIndexFile(INDEX_PATH + "lexicon.txt", INDEX_PATH + "finalIndex.txt");
		
		
		
		
	}
}
