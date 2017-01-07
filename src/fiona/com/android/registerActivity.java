package fiona.com.android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class registerActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 0;
	Button button;
	EditText name;
	EditText email;
	EditText pass1;
	EditText pass2;
	//EditText street;
	//EditText town;
	//EditText city;
	String userName;
	String ageString;
	String occupationString;
	String countryString;
	String streetString;
	String townString;
	String cityString;
	String genderString;
	RadioButton myRadio1, myRadio2;
	String url = "http://79.170.245.193/TestPhp/test22.php";
	File file = new File(Environment.getExternalStorageDirectory() + File.separator + "id.txt");
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	String btMac;
	Intent loginIntent;
	//label+"&variable2Name="+location+"&variable3Name="+date
	 BufferedReader in = null;
	 String contextResult = null;
	 HttpURLConnection connection;
	    OutputStreamWriter request = null;
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
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	        
        setContentView(R.layout.registration);
        
        loginIntent = new Intent(this,SensingEnvironmentActivity.class);
        
        genderString = "m";
    	
    	myRadio1 = (RadioButton)findViewById(R.id.radio0);
        myRadio2 = (RadioButton)findViewById(R.id.radio1);
        
       
		myRadio1.setOnClickListener(myOptionOnClickListener);
        myRadio2.setOnClickListener(myOptionOnClickListener);
        
        myRadio1.setChecked(true);
       
        
        
        
        Spinner ageSpinner = (Spinner) findViewById(R.id.spinner1);
        Spinner occSpinner = (Spinner) findViewById(R.id.spinner2);
        Spinner countrySpinner = (Spinner) findViewById(R.id.spinner3);
        String age[] = {"0-10","10-15","15-20","20-30","30-50", "50-70","70-100+"};
        String occupation[] = {"Student","Office Worker","Farmer","Builder","Medical Professional", "House-keeper","Train Driver"};
        String country[] = {"Africa","Agentina","Australia","Austria","Belgium","Brazil","Bulgaria","Ireland","Germany", "France", "Romania","United Kingdom","United States", "China"};
        
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, age);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        ageSpinner.setAdapter(spinnerArrayAdapter);
        
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, occupation);
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        occSpinner.setAdapter(spinnerArrayAdapter2);
        
        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, country);
        spinnerArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        countrySpinner.setAdapter(spinnerArrayAdapter3);
        
        ageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	ageString = parentView.getItemAtPosition(position).toString();
            	System.out.println(ageString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

			
        });
        
        occSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	occupationString = parentView.getItemAtPosition(position).toString();
            	System.out.println(occupationString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

			
        });
        
        countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	countryString = parentView.getItemAtPosition(position).toString();
            	System.out.println(countryString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

			
        });
        
        button = (Button) findViewById(R.id.button1);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        pass1 = (EditText) findViewById(R.id.pass1);
        pass2 = (EditText) findViewById(R.id.pass2);
        button.getBackground().setColorFilter(new LightingColorFilter(Color.BLUE, 1));//0xFFAA0000 ));
        
        
        
        
        Bundle b = getIntent().getExtras();

		if (b != null) {
			
			userName = b.getString("User");
		}
		
		
		btMac = getDeviceBluetoothMAC();
       
        button.setOnClickListener(
        		
        		new OnClickListener()
        		{

					@Override
					public void onClick(View v) {
						System.out.println("Button Clicked");
						boolean filledEditText = checkEditTexts();
						boolean passwordCheck = checkPassword();
						
						
					if ((filledEditText) && (passwordCheck)){							
						
						 try {
								file.createNewFile();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						     
						     BufferedWriter out = null;
							try {
								out = new BufferedWriter(new FileWriter(file));
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						     try {
								out.write(userName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						     
						     try {
								out.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

				/*xxxxxxx		
						HttpClient client = new DefaultHttpClient();


					    HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
					    // Limit
					    HttpResponse response;
					    JSONObject json = new JSONObject();
					    try {
					    	System.out.println("In http Post");
					       

					        HttpPost post = new HttpPost(url);

					        json.put("userName", userName);
					        json.put("name", name.getText());
					        json.put("street", countryString);
					        json.put("town", countryString);
					        json.put("city", countryString);
					        json.put("country", countryString);
					        json.put("gender", genderString);
					        json.put("email", email.getText());
					        json.put("age", ageString);
					        json.put("occupation", occupationString);
					        json.put("password", pass1.getText());
					        json.put("btMAC", btMac);
					       
					      //String jsonString = "{userName: 
					        
					        /*  BasicHttpParams params = new BasicHttpParams();
					        params.setParameter("test2",json.toString());
					        post.setParams(params);*/
			/*xxxx		       post.setHeader("test2", json.toString());
					        StringEntity se = new StringEntity(json.toString());
					        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					                "application/json"));
					        post.setEntity(se);
					        response = client.execute(post);
					        /* Checking response */
		/*xxxxx			        if (response != null) {
					            InputStream in = response.getEntity().getContent(); // Get the
					                                                                // data in
					                                                                    // the
					                                                                    // entity
					            String a = convertStreamToString(in);
					            Log.i("Read from Server", a);
					        }
					    } catch (Exception e) {
					        e.printStackTrace();
					    }

					    
					}xxxxx*/
						     
						     
							try
							{
							String param = "variable1Name=["+userName+","+name.getText()+","+ageString+","+email.getText()+","+genderString+","+occupationString+","+countryString+","+pass1.getText()+","+btMac+"]";	
							URL url =  new URL("http://79.170.245.193/TestPhp/test22.php");
							String response;
							connection = (HttpURLConnection) url.openConnection();
			                connection.setDoOutput(true);
			                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			                connection.setRequestMethod("POST");    

			                request = new OutputStreamWriter(connection.getOutputStream());
			                request.write(param);
			                request.flush();
			                request.close();            
			                String line = "";               
			                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
			                BufferedReader reader = new BufferedReader(isr);
			                StringBuilder sb = new StringBuilder();
			                while ((line = reader.readLine()) != null)
			                {
			                    sb.append(line + "\n");
			                }
			                
			                response = sb.toString();
			                // You can perform UI operations here
			                //Toast.makeText(this,"Message from Server: \n"+ response, 0).show();
			                System.out.println(response);
			                
			                if(response.contains("Connected Successfully")){
			                	Toast.makeText(registerActivity.this, "Your details have been recorded", Toast.LENGTH_LONG).show();
			                	Bundle b = new Bundle();
								b.putSerializable("User", userName);
								loginIntent.putExtras(b);
			                	startActivity(loginIntent);
			                	
			                }
			                else{
			                	Toast.makeText(registerActivity.this, "Unfortunately your details could not be recorded, Please retry entering the details", Toast.LENGTH_LONG).show();
			                	file.delete();
			                	finish();
			                }
							}
							catch (Exception ex)
							{
							//Exception handling
							}finally {

							      if(connection != null) {
							        connection.disconnect(); 
							      }
							    }	     
						     
					}else if ((filledEditText) && (!passwordCheck)){
						pass1.setText("");
						pass2.setText("");
						AlertDialog dialog = passwordDialogBox();
						dialog.show();
					}
					else if ((!filledEditText) && (passwordCheck)){
						AlertDialog dialog = blankEditTextDialogBox();
						dialog.show();
					}
					else{
						AlertDialog dialog = blankEditTextDialogBox();
						dialog.show();
					}
					}
					
        			
        		});
        
        
        
    }
	public boolean checkPassword(){
		boolean password;
		if (pass1.getText().toString().equals(pass2.getText().toString())){
			password = true;
		}
		else{
			password = false;
		}
		return password;
		
	}
	public boolean checkEditTexts(){
		//String blankEditText;
		boolean filled;
		if(name.getText().toString().equals("")){
			//blankEditText = "You must enter a name before completing registration";
			filled = false;
		}
		else if(pass1.getText().toString().equals("")){
			//blankEditText = "You must enter a password before completing registration";
			filled = false;
		}
		else if(pass2.getText().toString().equals("")){
			//blankEditText = "You must confirm your password before completing registration";
			filled = false;
		}
	/*	else if(street.getText().toString().equals("")){
			//blankEditText = "You must enter a password before completing registration";
			filled = false;
		}
		else if(town.getText().toString().equals("")){
			//blankEditText = "You must confirm your password before completing registration";
			filled = false;
		}
		else if(city.getText().toString().equals("")){
			//blankEditText = "You must confirm your password before completing registration";
			filled = false;
		}*/
		else{
			filled = true;
		}
		return filled;
		
	}
	private AlertDialog blankEditTextDialogBox() {
 		// Builder to object to specify optional arguments
 		AlertDialog detailsDialog = new AlertDialog.Builder(this)
 				// sets title and message to be displayed by dialog
 				.setTitle("Blank Text Field")
 				.setMessage("All textfields marked * must be filled in with appropiate details.")
 				// this activity is ended when yes is selected
 				.setPositiveButton("OK",
 						new DialogInterface.OnClickListener() {

 							@Override
 							public void onClick(DialogInterface dialog,
 									int which) {
 								dialog.cancel();

 							}
 						}).create(); // creates builder object and returns dialogbox to
 								// call method
 		return detailsDialog;

 	}
	private AlertDialog passwordDialogBox() {
 		// Builder to object to specify optional arguments
 		AlertDialog detailsDialog = new AlertDialog.Builder(this)
 				// sets title and message to be displayed by dialog
 				.setTitle("Incompatible Password")
 				.setMessage("Your passwords do not match! Please re-enter your password.")
 				// this activity is ended when yes is selected
 				.setPositiveButton("OK",
 						new DialogInterface.OnClickListener() {

 							@Override
 							public void onClick(DialogInterface dialog,
 									int which) {
 								dialog.cancel();

 							}
 						}).create(); // creates builder object and returns dialogbox to
 								// call method
 		return detailsDialog;

 	}
	
	RadioButton.OnClickListener myOptionOnClickListener =
 		   new RadioButton.OnClickListener()
 		  {

 		  @Override
 		  public void onClick(View v) {
 			  if(myRadio1.isChecked()){
 					genderString = "m";
 				}
 		        else{
 		        	genderString = "f";
 		        }
 			 System.out.println(genderString);
 		  }
 		  
	  };
	public String getDeviceBluetoothMAC(){
		String mac = null;
		if (mBluetoothAdapter != null) {
			/*if (!mBluetoothAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    mac = BluetoothAdapter.getDefaultAdapter().getAddress();
			}*/
			mac = "tgrngklnergr";
		}
		return mac;
		
	}
	private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
