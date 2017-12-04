package hw3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IndexReader {
	private RandomAccessFile randomAccessIndexFile;
	
	public void loadIndex(String indexPath) throws FileNotFoundException{
		randomAccessIndexFile = new RandomAccessFile(indexPath,"r");
	}
	
	public byte[] readIndex(String coordinate) throws IOException{
		String[] val = coordinate.split(",");
		int position = Integer.parseInt(val[0].trim());
		int length = Integer.parseInt(val[1].trim());
		randomAccessIndexFile.seek(position);
		byte[] compressed = new byte[length];
		randomAccessIndexFile.read(compressed);
		return compressed;
	}

}
