package hw3;

import java.util.*;

public class BM25 {
    private IndexList indexList;
    
    private String[] queryTerms;
    
    private ArrayList<ArrayList<String>> pageTables;
    
    private final double b = 0.75;
    
    private final double k1 = 1.2;
    
    private int docNum = Integer.MAX_VALUE;
    
    private double avgDocLen = Integer.MAX_VALUE;
    
    public BM25(IndexList indexList, String[] queryTerms, ArrayList<ArrayList<String>> pageTables){
        this.indexList = indexList;
        this.queryTerms = queryTerms;
        this.pageTables = pageTables;
    }
    
    public void setDocNum(int docNum){
    	this.docNum = docNum;
    }
    public void setAvgDocLen(double avgDocLen){
    	this.avgDocLen = avgDocLen;
    }

    public double calcScore(int docId){
        double score = 0;				       
        String page = getPage(pageTables, docId);
        
        double d = Double.parseDouble(page.substring(page.lastIndexOf(",") + 1));    // length of given document
        double K = k1 * ((1 - b) + b * d / avgDocLen);

        for(String term : queryTerms){
        	Index index = indexList.openList(term);
        	if(!index.contains(docId)) continue;
            int fi = index.getFreq(docId);                                  
            int ft = index.size();            // num docs containing term i
            int N = docNum;                  
            
            double idf = Math.log((N - ft + 0.5) / (ft + 0.5));
            score += idf * (((k1 + 1) * fi) / (K + fi));           
        }
        return score;
    }
    
    public String getPage(ArrayList<ArrayList<String>> pageTables, int docId){
    	int i = docId / 500000;
    	return pageTables.get(i).get(docId % 500000);
    }

}
