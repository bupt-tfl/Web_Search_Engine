package hw3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class QueryProcessor {

	private List<HashMap<String, String>> lexicons;

	private ArrayList<ArrayList<String>> pageTables;

	private IndexReader indexReader;

	private SnippetFinder snippetFinder = new SnippetFinder();
	
	public QueryProcessor(List<HashMap<String, String>> lexicons, ArrayList<ArrayList<String>> pageTables, IndexReader indexReader){
		this.lexicons = lexicons;
		this.pageTables = pageTables;
		this.indexReader = indexReader;
	}

	public String[] parseQuery(String query){
		String[] queryTerms = query.toLowerCase().split(" ");
		return queryTerms;
	}

	public void disjunctiveProcess(String ... queries) throws IOException{
		IndexList indexList = open(queries);
		String[] terms = indexList.getTerms();
		BM25 bm = new BM25(indexList, terms, pageTables);
		bm.setAvgDocLen(avgDocLen());
		bm.setDocNum(docNum());
		PriorityQueue<Doc> res = new PriorityQueue<>(new Comparator<Doc>(){
			@Override
			public int compare(Doc d1, Doc d2){
				if(d2.getScore() - d1.getScore() > 0) return 1;
				else if(d2.getScore() == d1.getScore()) return 0;
				else return -1;
			}
		});
		for(int i = 0; i < terms.length; i ++){
			int docId = 0;
			while(true){
				docId = nextGEQ(indexList, terms[i], docId);
				if(docId > docNum()) break;
				double score = bm.calcScore(docId);
				res.add(new Doc(docId, score));
				docId ++;
			}
		}
		for(int i = 0; i < 10; i ++){
			if(res.isEmpty()) break;
			Doc doc = res.poll();
			String url = getUrl(doc.getId());
			url = url.substring(0, url.lastIndexOf(",")).trim();
			System.out.println(String.format("%s %f", url, doc.getScore()));
			for(String term : terms)
				System.out.println(snippetFinder.getSnippet(url, term));
		}
	}

	public void conjunctiveProcess(String ... queries) throws IOException{
		IndexList indexList = open(queries);
		String[] terms = indexList.getTerms();
		BM25 bm = new BM25(indexList, terms, pageTables);
		bm.setAvgDocLen(avgDocLen());
		bm.setDocNum(docNum());
		PriorityQueue<Doc> res = new PriorityQueue<>(new Comparator<Doc>(){
			@Override
			public int compare(Doc d1, Doc d2){
				if(d2.getScore() - d1.getScore() > 0) return 1;
				else if(d2.getScore() == d1.getScore()) return 0;
				else return -1;
			}
		});
		int docId = 0;
		while(true){
			docId = nextGEQ(indexList, terms[0], docId);
			if(docId > docNum()) break;
			int d = 0;
			for(int i = 1; (i<terms.length) && ((d = nextGEQ(indexList,terms[i],docId)) == docId) ; i++);
			if(d > docId) docId = d;
			else{
				double score = bm.calcScore(docId);
				res.add(new Doc(docId, score));
				docId ++;
			}
		}
		for(int i = 0; i < 20; i ++){
			if(res.isEmpty()) break;
			Doc doc = res.poll();
			String url = getUrl(doc.getId());
			url = url.substring(0, url.lastIndexOf(",")).trim();
			System.out.println(String.format("%s %f", url, doc.getScore()));
			for(String term : terms)
				System.out.println(snippetFinder.getSnippet(url, term));
		}
	}

	public IndexList open(String ... queries) throws IOException{
		IndexList index = new IndexList();
		for(String query : queries){
			String position = searchTerm(query);
			if(position != null)
				index.addIndex(query, new Index(query, indexReader.readIndex(position)));
		}
		return index;
	}

	public String searchTerm(String term){
		for(HashMap<String, String> map : lexicons){
			if(map.containsKey(term)) return map.get(term);
		}
		System.out.println(String.format("didn't find %s", term));
		return null;
	}

	public double avgDocLen(){
		long len = 0;
		int docNum = docNum();
		for(ArrayList<String> pageTable : pageTables){
			for(String page : pageTable){
				len += Long.parseLong(page.substring(page.lastIndexOf(",")+1));
			}
		}
		return (double)len / docNum;
	}

	public int docNum(){
		int len = 0;
		for(ArrayList<String> pageTable : pageTables){
			len += pageTable.size();
		}
		return len;
	}

	public String getUrl(int docId){
		int i = docId / 500000;
		return pageTables.get(i).get(docId % 500000);
	}

	public int nextGEQ(IndexList indexList, String term, int k){
		return indexList.openList(term).nextGEQ(k);
	}

}
