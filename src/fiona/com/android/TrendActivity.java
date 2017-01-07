package fiona.com.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class TrendActivity extends Activity{
	Button button;
	RadioButton myRadio1, myRadio2, myRadio3, myRadio4;
	int choice;
	String formattedDate;
	double lat;
	double longt;
	String nameString;
	List<Address> name;
	String address;
	String addressLoc;
	String formattedLoc;
	boolean automaticUpdates;
	String timeOfDay = null;
    HttpURLConnection connection;
    OutputStreamWriter request = null;
    String user = null;
    URL url = null;   
    String response = null;         
    String parameters = null;
    int weekday;
    int locationChangesCount;
    private boolean mReceivingUpdates = false;
    LocationManager lm;
    LocationListener ll;
    Timer timer;
    boolean gpsAvail;
    long start;
    long end;
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	        
        setContentView(R.layout.patternlookup);
        
        
        
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    ll = new mylocationlistener();
	    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
        mReceivingUpdates = true;
        
        button = (Button) findViewById(R.id.button1);
        button.getBackground().setColorFilter(new LightingColorFilter(Color.BLUE, 0));//0xFFAA0000 ));
        
        
        myRadio1 = (RadioButton)findViewById(R.id.radio0);
        myRadio2 = (RadioButton)findViewById(R.id.radio1);
        myRadio3 = (RadioButton)findViewById(R.id.radio2);
        myRadio4 = (RadioButton)findViewById(R.id.radio3);
        
       
		myRadio1.setOnClickListener(myOptionOnClickListener);
        myRadio2.setOnClickListener(myOptionOnClickListener);
        myRadio3.setOnClickListener(myOptionOnClickListener);
        myRadio4.setOnClickListener(myOptionOnClickListener);
        
        myRadio4.setChecked(true);
        choice = 4;
        automaticUpdates = false;
        locationChangesCount = 0;//6;
        button.setOnClickListener(
        		
        		new OnClickListener()
        		{

					@Override
					public void onClick(View v) {
						getTimeAndDay();
						getUser();
						
					     if(choice == 1){
					    	 parameters = "variable1="+Integer.toString(weekday)+"&variable3="+"["+formattedLoc+"]"+"&variable4="+user;
					    	 getTrendFromServer();
					    	// mylocationlistener listen = new mylocationlistener();
					       //  listen.stopUpdates();
					     }
					     else if (choice == 2){
					    	 parameters = "variable2="+timeOfDay+"&variable3="+"["+formattedLoc+"]"+"&variable4="+user;
					    	 getTrendFromServer();
					    	// mylocationlistener listen = new mylocationlistener();
					        // listen.stopUpdates();
					     }
					     else if (choice == 3) {
					    	 parameters = "variable1="+ Integer.toString(weekday)+"&variable2="+timeOfDay+"&variable3="+"["+formattedLoc+"]"+"&variable4="+user;
					    	 getTrendFromServer();
					    	// mylocationlistener listen = new mylocationlistener();
					        // listen.stopUpdates();
					     }
					     else if (choice == 4) {
					    	 automaticUpdates = true;
					    	 locationChangesCount = 5;
					    	 //timer = new Timer();
					        // timer.schedule(new RemindTask(), 5000);
					    	// getTimeAndDay();
							//	getUser();
					    	// parameters = "variable1="+ Integer.toString(weekday)+"&variable2="+timeOfDay+"&variable3="+"["+formattedLoc+"]"+"&variable4="+user;
							//	getTrendFromServer();
					    	 start = System.currentTimeMillis();
					    	 end = System.currentTimeMillis() + 100000;
					    	 Thread thread = new gpsThread();
					    	 thread.start();
					    	 
					     
					     }
					     else {
					    	 System.out.println("Nothing for parameters");
					     }
					   
					    // System.out.println("PARAs: "+parameters);
					     
					}
        			        			
        			
        		});
               			
        		}
	
	class gpsThread extends Thread {
	    // This method is called when the thread runs
	    public void run() {
	    	while(System.currentTimeMillis() < end){
	    		if(name == null){
	            	setLocName(false);
	            }
	            else{
	            	setLocName(true);
	            }
	    		
	    	}
	    	
	    	if(!getLocName()){
	    		runOnUiThread(new Runnable() {
				    @Override
				    public void run() {
				    	Toast.makeText(getApplicationContext(), "Sorry Location could not be determined", Toast.LENGTH_LONG).show();
				    }
				});
	    	}
	    }
	}
	
	/*public void onStart(){
		super.onStart();
		automaticUpdates = false;
        locationChangesCount = 6;
	}*/
	
	public void setLocName(boolean name){
		this.gpsAvail = name;
	}
	
	public boolean getLocName(){
		return gpsAvail;
	}

    class RemindTask extends TimerTask {
        public void run() {
            if(name == null){
            	setLocName(false);
            }
            else{
            	setLocName(true);
            }
            
        } 
        
    }
	public void getUser(){
		 File file = new File(Environment.getExternalStorageDirectory() + File.separator + "id.txt");
			
			try {
				StringBuilder text = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader(file));
				user = br.readLine();
				text.append(user);
				
				System.out.println("ID: "+user);
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void getTimeAndDay(){
		Calendar c = Calendar.getInstance();
	    // = "username="+mUsername+"&password="+mPassword;   


	     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     formattedDate = df.format(c.getTime());
		
	     weekday = c.get(Calendar.DAY_OF_WEEK);
	     int hour = c.get(Calendar.HOUR_OF_DAY);
		
	     if(hour < 12){
		    	timeOfDay = "morning";
		    }
		    else if (12 < hour && hour < 13){
		    	timeOfDay = "midday";
		    }
		    else if (13 < hour && hour < 16){
		    	timeOfDay = "afternoon";
		    }
		    else if (16 < hour && hour < 20){
		    	timeOfDay = "evening";
		    }
		    else if (20 < hour && hour < 24){
		    	timeOfDay = "night";
		    }
	}
	
	public void getTrendFromServer(){
	//	if (formattedLoc != null) {
	    	 
		     try
	            {
	                url = new URL("http://79.170.245.193/TestPhp/test3.php");
	                connection = (HttpURLConnection) url.openConnection();
	                connection.setDoOutput(true);
	                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	                connection.setRequestMethod("POST");    

	                request = new OutputStreamWriter(connection.getOutputStream());
	                request.write(parameters);
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
	                // Response from server after login process will be stored in response variable.                
	                response = sb.toString();
	                // You can perform UI operations here
	                //Toast.makeText(this,"Message from Server: \n"+ response, 0).show();
	                System.out.println(response);
	                String context = null;
	                String count = null;
	              /*  Pattern contextPattern = Pattern.compile("Context:");
		    		  Matcher contextMatch = contextPattern.matcher(response);
		    		  if (contextMatch.find()) {
		    		    System.out.println(contextMatch.group(1)); // => "3"
		    		    context = contextMatch.group(1);
		    		  }
		    		  
		    		  Pattern countPattern = Pattern.compile("Count:");
		    		  Matcher countMatch = countPattern.matcher(response);
		    		  if (countMatch.find()) {
		    		    System.out.println(countMatch.group(1)); // => "3"
		    		    count = countMatch.group(1);
		    		  }*/
	                
	                context = response.substring(response.indexOf("*")+1, response.lastIndexOf("*"));
	                count = response.substring(response.lastIndexOf("*")+1);
	                System.out.println("Context "+context);
	                if(context.equals("")){
	                	Toast.makeText(TrendActivity.this, "No pattern has been detected!", Toast.LENGTH_LONG).show();
			    		  
	                }
	                else{																//"+count+"
		    		  Toast.makeText(TrendActivity.this, "Pattern Detected! "+context+" sound occured on multiple occasions", Toast.LENGTH_LONG).show();
	                }
	                
	                isr.close();
	                reader.close();

	                
	                
	            }
	            catch(IOException e)
	            {
	                // Error
	            }
	/*	    } else{
		    	AlertDialog dialog = locationDialogBox();
				dialog.show();
		    }*/
	}
	private AlertDialog locationDialogBox() {
 		// Builder to object to specify optional arguments
 		AlertDialog detailsDialog = new AlertDialog.Builder(this)
 				// sets title and message to be displayed by dialog
 				.setTitle("Invalid Location")
 				.setMessage("Sorry a valid location has not been detected. Please retry again in a minute.")
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
	 					choice = 1;
	 				}
	 		        else if(myRadio2.isChecked()){
	 		        	choice = 2;
	 		        }
	 		       else if(myRadio3.isChecked()){
	 		        	choice = 3;
	 		        }
	 		      else if(myRadio4.isChecked()){
	 		        	choice = 4;
	 		        }
	 		        else{
	 		        	choice = 1;
	 		        }
	 			  
	 			 
	 		  }
	 		  
		  };
        
		 public void checkForAutoUpdate(){
			 if((automaticUpdates) && (locationChangesCount > 0)){
				 getTimeAndDay();
				 getUser();
		    	 parameters = "variable1="+ Integer.toString(weekday)+"&variable2="+timeOfDay+"&variable3="+"["+formattedLoc+"]"+"&variable4="+user;
				 getTrendFromServer();	
				 locationChangesCount--;
				 System.out.println("in 1st if");
				 				 
			 }
			 else if((automaticUpdates = true) && (!(locationChangesCount > 0))){
				 	mylocationlistener listen = new mylocationlistener();
		        	listen.stopUpdates();
		        	//mReceivingUpdates = false;
		        	automaticUpdates = false;
		        	locationChangesCount = 0;
		        	System.out.println("in 2nd if");
			 }
			 
			 System.out.println("LocChanges "+locationChangesCount);
		 } 
		 
		  public class mylocationlistener implements LocationListener {
				
				Geocoder gCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
				
			@Override
			public void onLocationChanged(Location location) {
			nameString = "Default";
				System.out.println("NameString "+nameString);
			    if (location != null) {
			    	
			    	lat = location.getLatitude();
			    	longt = location.getLongitude();	
			    	
			    Log.d("LOCATION CHANGED", location.getLatitude() + "");
			    Log.d("LOCATION CHANGED", location.getLongitude() + "");
			    
			    //locationChangesCount++;
			    
			    
			    
				try {
					name = gCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
					if(name.equals(null)){
						 nameString = "Location Name could not be detected BUT GPS coordinates are"+lat+","+longt;
					}
					else{
						nameString = name.get(0).getLocality();
						String add1 = name.get(0).getAdminArea();
						String add2 = name.get(0).getCountryCode();
						String add3 = name.get(0).getCountryName();
						String add4 = name.get(0).getFeatureName();
						String add5 = name.get(0).getLocality();
						String add6 = name.get(0).getPhone();
						String add7 = name.get(0).getPostalCode();
						String add8 = name.get(0).getPremises();
						String add9 = name.get(0).getSubAdminArea();
						String add10 = name.get(0).getSubLocality();
						String add11 = name.get(0).getPostalCode();
						if(add5 == null){
							add5 = "Unspecified Locality";
							}
						
						System.out.println(" Feature Name "+add4+" Sub Loc "+add10+" Sub Area "+add9+" Address Area "+add1+
								" Country Name "+add3+" Country Code "+add2+" Locality "+add5+" Postcode "+add11);
					address = add4+","+add10+","+add5+","+add9+","+add1+","+add3+","+add2;
						
					
					
					if(add10 == null){
						add10 = "Unknown_Street";
					}
					if(add9 == null){
						add9 = "Unknown_Locality";
					}
					if(add1 == null){
						add1 = "Unknown_City";
					}
					if(add3 == null){
						add3 = "Unknown_Country";
					}
					
					addressLoc = add10+","+add9+","+add1+","+add3;
					
						if(addressLoc != null){
							formattedLoc = addressLoc.replace(" ", "_");
						}
						
						System.out.println("Name "+name);
						System.out.println("Address "+address);
					}
				} catch (IOException e) {
					System.out.println("Problem Acquiring Location name "+ e);
					e.printStackTrace();
				}
				
						
			    }
			    System.out.println("Address Location: "+addressLoc);
			    System.out.println("Formatted Location: "+formattedLoc);
			    checkForAutoUpdate();
			    	
			}

			public void stopUpdates(){
				/*if ( mReceivingUpdates)
		        {
					lm.removeUpdates(this);
		            mReceivingUpdates = false;
		        }*/
				if(name != null){
		        	lm.removeUpdates(ll);
		        	lm = null;
		        	ll = null;
		        	name = null;
		        }
				
			}
			
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			}
	/*	  @Override 
		    public void onPause()
		    {
		        super.onPause();

		        if ( mReceivingUpdates)
		        {
		        	mylocationlistener listen = new mylocationlistener();
		        	listen.stopUpdates();
		            mReceivingUpdates = false;
		        }
		    }

		    @Override
		    public void onResume()
		    {
		        super.onResume();
		        if (! mReceivingUpdates)
		        {
		        	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
		            mReceivingUpdates = true;
		        }
		    }
*/
		    @Override
		    public void onDestroy()
		    {
		      //  if ( mReceivingUpdates )
		      //  {
		        	//mylocationlistener listen = new mylocationlistener();
		        	//listen.stopUpdates();
		       //     mReceivingUpdates = false;
		       // }

		      //  lm = null;
		    	if((automaticUpdates = false) ){
				 	mylocationlistener listen = new mylocationlistener();
		        	listen.stopUpdates();
		        	
			 }
		        super.onDestroy();
		        
		    }

}
