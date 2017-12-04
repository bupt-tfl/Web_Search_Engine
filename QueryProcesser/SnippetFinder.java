package hw3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SnippetFinder {
	public String getSnippet(String strURL, String term) throws IOException {

        String result = "";
        
        try {
            URL url = new URL(strURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStreamReader input = new InputStreamReader(httpConn.getInputStream(), "utf-8");
            BufferedReader bufReader = new BufferedReader(input);

            String line = "";

            StringBuilder contentBuf = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                contentBuf.append(line);
            }

            String buf = contentBuf.toString();

            int beginIx = buf.indexOf(term);
            int endIx = buf.lastIndexOf(term);

            if (beginIx == endIx) {
                result = buf.substring(Math.max(0, beginIx - 20), Math.min(endIx + 20, buf.length() - 1));
            } else {
                result = buf.substring(Math.max(0, beginIx - 10), Math.min(beginIx + 10, buf.length() - 1));
                result += "....." + buf.substring(Math.max(0, endIx - 10), Math.min(endIx + 10, buf.length() - 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        result = "....." + result + ".....";

        return result;
    }
}
