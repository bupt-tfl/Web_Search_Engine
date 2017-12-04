package hw3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static final String INDEX_PATH = "D:\\fall2017\\web search engine\\hw2\\inverted_index\\data\\index_files\\";

	private static IndexReader indexReader;

	private static Scanner sc;
	private static List<HashMap<String, String>> lexicon;
	private static QueryProcessor qp;

	public static void main(String[] args) throws IOException{
		LexiconLoader lexiconLoader = new LexiconLoader();

		long time = System.currentTimeMillis();
		lexicon = lexiconLoader.loadLexicon(INDEX_PATH + "lexicon.txt");
		System.out.println(lexicon.size());
		System.out.println(String.format("lexicon load time: %d",System.currentTimeMillis()-time));
		indexReader = new IndexReader();
		indexReader.loadIndex(INDEX_PATH + "finalIndex.txt");
		long time2 = System.currentTimeMillis();
		PageTableLoader pageTableLoader = new PageTableLoader();
		ArrayList<ArrayList<String>> pageTable = pageTableLoader.load(INDEX_PATH + "pageTable.txt");
		System.out.println(String.format("pageTable load time: %d", System.currentTimeMillis() - time2));




		qp = new QueryProcessor(lexicon, pageTable, indexReader);

		while(true){
			sc = new Scanner(System.in);
			System.out.println("Please choose mode:\n 1: conjunctiveProcess\n 2: disjunctiveProcess");
			String mode = sc.nextLine();
			if(Integer.parseInt(mode) == 1)
				conjunctiveProcess();
			else if(Integer.parseInt(mode) == 2)
				disjunctiveProcess();
			else
				System.out.println("invalid mode");
		}

	}

	public static String searchItem(String item){
		for(HashMap<String, String> map : lexicon){
			if(map.containsKey(item)) return map.get(item);
			continue;
		}
		return null;
	}
	public static void conjunctiveProcess() throws IOException{
		sc = new Scanner(System.in);
		System.out.println("Please input key word:");
		String query = sc.nextLine();
		String[] queries = qp.parseQuery(query);
		qp.conjunctiveProcess(queries);
	}

	public static void disjunctiveProcess() throws IOException{
		sc = new Scanner(System.in);
		System.out.println("Please input key word:");
		String query = sc.nextLine();
		String[] queries = qp.parseQuery(query);
		qp.disjunctiveProcess(queries);
	}
}
