package hw3;

public class Doc{
	private int docId;
	
	private double score;
	
	public Doc(int docId, double score){
		this.docId = docId;
		this.score = score;
	}
	
	public double getScore(){
		return this.score;
	}
	
	public int getId(){
		return this.docId;
	}
}
