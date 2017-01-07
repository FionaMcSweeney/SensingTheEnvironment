package fiona.com.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Environment;
import android.util.Log;

public class ServerInterface {
	static HttpURLConnection connection = null;
	static DataOutputStream outputStream = null;
	DataInputStream inputStream = null;

	//String pathToOurFile = "/data/file_to_send.mp3";
	static String urlServer = "http://79.170.245.193/BackendListener/ReceiveVariables.php";
	static String lineEnd = "\r\n";
	static String twoHyphens = "--";
	static String boundary =  "*****";

	static int bytesRead;
	static int bytesAvailable;
	static int bufferSize;
	static byte[] buffer;	
	static int maxBufferSize = 1*1024*1024;
	static List<NameValuePair> nameValuePairs;
	
	
	private static String executeHttpRequest(String data) {
	//String executeHttpRequest(String data) {
	    String result = "";
	    InputStream is = null;
	    System.out.println("In Connection Class");
	    HttpResponse response;
	    try{
	    	
	    	            HttpClient httpclient = new DefaultHttpClient();
	    	            
	    	            
	    	            nameValuePairs = new ArrayList<NameValuePair>(2);
	    	            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
	    	            nameValuePairs.add(new BasicNameValuePair("username","Fiona"));  // $Edittext_value = $_POST['Edittext_value'];
	    	            nameValuePairs.add(new BasicNameValuePair("password","Password"));
	    	            HttpPost httppost = new HttpPost("http://192.168.2.103/BackendListener/ReceiveVariables.php");
	    	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    	
	    	             response = httpclient.execute(httppost);
	    	
	    	            HttpEntity entity = response.getEntity();
	    	
	    	            is = entity.getContent();
	    	
	    	        }catch(Exception e){
	    	
	    	            Log.e("log_tag", "Error in http connection"+e.toString());
	    	
	    	        }

	
	 
	   return result;
	}

	public static String getContext() {
	    /*
	     * Let's construct the query string. It should be a key/value pair. In
	     * this case, we just need to specify the command, so no additional
	     * arguments are needed.
	     */
		System.out.println("In getContext Class");
	    String data = "command=" + URLEncoder.encode("getContext");
	    return executeHttpRequest(data);
	}
}
