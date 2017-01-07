package fiona.com.android;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.StringUtils;

import android.util.Log;

public class SendDataToServer {
	HttpURLConnection connection = null;
	DataOutputStream outputStream = null;
	DataInputStream inputStream = null;

	String pathToOurFile;// = "/data/file_to_send.mp3";
	String urlServer;// = "http://192.168.2.103/TestPhp/test.php?argument1Name=nothing";
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary =  "*****";

	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 1*1024*1024;
	String charset = "UTF-8";
	String address;

	public String sendData(String path, String param){//String label, String location, String date, String time, String contextVisibility){
		pathToOurFile = path;
		System.out.println(pathToOurFile);
		urlServer = "http://79.170.245.193/TestPhp/test.php?variable1Name="+param;//label+"&variable2Name="+location+"&variable3Name="+date
		 BufferedReader in = null;
		 String contextResult = null;
		try
		{
		FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

		URL url = new URL(urlServer);
		connection = (HttpURLConnection) url.openConnection();

		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		// Enable POST method
		connection.setRequestMethod("POST");
		connection.setRequestMethod("GET");

		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
		outputStream = new DataOutputStream( connection.getOutputStream() );
		outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
		outputStream.writeBytes(lineEnd);

		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		buffer = new byte[bufferSize];

		// Read file
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		System.out.println("BEFORE RESPONSE!!");
		while (bytesRead > 0)
		{
		outputStream.write(buffer, 0, bufferSize);
		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}

		outputStream.writeBytes(lineEnd);
		outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// Responses from the server (code and message)
	 InputStream is = connection.getInputStream();
	 
	 
 
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	        	      }
	      
	      
	     
	      String responseString = response.toString();
	      System.out.println("RESPONSE: "+responseString);
	      
	     
	      Pattern p = Pattern.compile("<<Response>>(.*?)<<Response>>");
	    		  Matcher m = p.matcher(responseString);
	    		  if (m.find()) {
	    		    System.out.println(m.group(1)); // => "3"
	    		    contextResult = m.group(1);
	    		    	    		    
	    		  }
	    		  Pattern loc = Pattern.compile("<<Location>>(.*?)<<Location>>");
	    		  Matcher mLoc = loc.matcher(responseString);
	    		  if (mLoc.find()) {
	    		    System.out.println(mLoc.group(1)); // => "3"
	    		    String add = mLoc.group(1);
	    		    setLocationFromServer(add);
	    		  }
			
	      rd.close();
	 
		
		int serverResponseCode = connection.getResponseCode();
		String serverResponseMessage = connection.getResponseMessage();
		System.out.println("ServerResponseCode: "+serverResponseCode+" "+"ServerResponseMessage: "+serverResponseMessage);

		
		fileInputStream.close();
		outputStream.flush();
		outputStream.close();
		is.close();
		}
		catch (Exception ex)
		{
		//Exception handling
		}finally {

		      if(connection != null) {
		        connection.disconnect(); 
		      }
		    }
		return contextResult;
		
	}
	
	public void setLocationFromServer(String add){
		this.address = add;
	}
	public String getLocationFromServer(){		
		return address;
		
	}
}
