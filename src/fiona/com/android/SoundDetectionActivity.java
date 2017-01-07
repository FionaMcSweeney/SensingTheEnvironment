package fiona.com.android;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SoundDetectionActivity extends Activity{
	TextView t1;
	TextView t2;
	TextView t3;
	Button button;
	Button button2;
	Button button3;
	Intent intent;
	Intent changeIntent;
	SensingEnvironmentService service;
	Bundle changeLabel;
	String [] result;
	BluetoothService bluetooth = new BluetoothService();
	Intent btService;
	protected static final int REQUEST_ENABLE_BT = 0;
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BroadcastReceiver mReceiver;
	BluetoothDevice device;
	Vector<BluetoothDevice> vec = new Vector<BluetoothDevice>();
	String [] devName;
	String sound;
	BluetoothDevice MAC;
	String stringUUID = "a7164340-e938-11e1-aff1-0800200c9a66";
    UUID uuid = UUID.fromString(stringUUID);    
    String NAME = "SensingEnvironment";
    BluetoothDevice devMAC;
    String message;
	//Spinner btSpinner;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.displaysoundlayout);
	        t1 = (TextView)findViewById(R.id.textView1);
	        t2 = (TextView)findViewById(R.id.textView2);
	        t3 = (TextView)findViewById(R.id.textView3);
	        button = (Button) findViewById(R.id.button1);
	        button2 = (Button) findViewById(R.id.button2);
	        button3 = (Button) findViewById(R.id.button3);
	        button.getBackground().setColorFilter(new LightingColorFilter(Color.BLUE, 1));//0xFFAA0000 ));
	        button2.getBackground().setColorFilter(new LightingColorFilter(Color.BLUE, 1));//0xFFAA0000 ));
	        button3.getBackground().setColorFilter(new LightingColorFilter(Color.BLUE, 1));//0xFFAA0000 ));
	        
	        //btSpinner = (Spinner) findViewById(R.id.spinner1);
	        intent = new Intent(this, SensingEnvironmentService.class);
	        changeIntent = new Intent(this, ChangeLabel.class);
	        btService = new Intent(this,BluetoothService.class);
	        service = new SensingEnvironmentService();
	        Bundle b = getIntent().getExtras();
	        changeLabel = new Bundle();
	        //final BluetoothService blueSrv = new BluetoothService();
	       // btSpinner.setVisibility(View.INVISIBLE);
	        
	        
			if(b != null){
				result = b.getStringArray("result");
				t1.setText("A Sound similar to "+result[0]+" has been detected");
				t2.setText("Date/Time of Detection: "+result[1]);
				t3.setText("Address: "+result[2]);
				
			}
			else { 	t1.setText("Sound Error Occured");
					t2.setText("Date Error Occured");
					t3.setText("Location Error Occured");
					}
			
			sound = result[0];
			
			
			boolean bt = checkBtEnabled();
			if (!bt){
				Toast.makeText(this, "This device does not support Bluetooth", Toast.LENGTH_LONG).show();
			}
			else{
				try{
					//setBTDiscoverable();
					getPairedDevices();
					
					 Spinner btSpinner = (Spinner) findViewById(R.id.spinner1);
					 String catArray[] = {"Innate Object","Human","Animal", "Weather"};
				        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, devName);
				        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
				        btSpinner.setAdapter(spinnerArrayAdapter1);
					
					
				        btSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				            @Override
				            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				            	System.out.println("Position "+position);
				            	MAC = vec.elementAt(position);
				            	System.out.println("MAC "+MAC);
				            }

				            @Override
				            public void onNothingSelected(AdapterView<?> parentView) {
				                // your code here
				            }

							
				        });
				}catch(Exception e){
					System.out.println("Exception "+e);
				}
				
			}
			
			
			
			
			button.setOnClickListener(
					
					new OnClickListener()
					{

						@Override
						public void onClick(View v) {
							File file = new File(Environment.getExternalStorageDirectory() + File.separator + "id.txt");
							try {
								StringBuilder text = new StringBuilder();
								BufferedReader br = new BufferedReader(new FileReader(file));
								String line = br.readLine();
								text.append(line);
								
								Bundle user = new Bundle();
								user.putSerializable("User", line);
								intent.putExtras(user);
								
								startService(intent);
								finish();
								Log.i("Test", "Listening Start again");
								
								System.out.println("ID: "+line);
								br.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
						       			
					});
			button2.setOnClickListener(
					
					new OnClickListener()
					{

						@Override
						public void onClick(View v) {
							/*if(isMyServiceRunning()){
								finish();
								
							//	stopService(intent);
						
								Log.i("Test", "Closing Application");
							}*/
							changeLabel.putStringArray("details", result);
							changeIntent.putExtras(changeLabel);
							startActivity(changeIntent);
							
						}
						       			
					});
			
			button3.setOnClickListener(
					
					new OnClickListener()
					{

						@Override
						public void onClick(View v) {
							//boolean connection = bluetooth.getAvailConnection();
							//if(isMyBluetoothServiceRunning()){
							/*	try{
									Thread t1=new ConnectThread();
									//ConnectThread connectT = new ConnectThread();
									t1.start();
									//bluetooth.connectToDevice(sound, MAC);
									System.out.println("Called the connect method");
								}catch(Exception e){
									Toast.makeText(SoundDetectionActivity.this, "Sorry cannot perform this operation", Toast.LENGTH_LONG).show();
									System.out.println("Exception "+e);
								}
							//}
							else{*/
								try{
									startService(btService);
									bluetooth.connectToDevice(sound, MAC);
								}catch(Exception e){
									Toast.makeText(SoundDetectionActivity.this, "Sorry cannot perform this operation", Toast.LENGTH_LONG).show();
									System.out.println("Exception "+e);
								}
							}
							      			 	       			    
	       			//};	       									       			
					
				});
	    }
	 public boolean checkBtEnabled(){
		 boolean bt;
		 if (mBluetoothAdapter == null) {
			 bt = false;
			}else{
				bt = true;
			}
		 return bt;
	 }
	 
	 public void setBTDiscoverable(){
		 Intent discoverableIntent = new
				 Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				 discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				 startActivity(discoverableIntent);
	 }
	 
	 public void getPairedDevices(){
		 Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		        vec.add(device);
		       
		        
		    }
		    devName = new String[vec.size()];
	        for(int i = 0; i<vec.size(); i++)
	        {
	        devName[i]= vec.elementAt(i).getName();//device.getName().toString();
	        }
		}
	 }
	 
	 private boolean isMyBluetoothServiceRunning() {
	        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	            if ("fiona.com.android.BluetoothService".equals(service.service.getClassName())) {
	                return true;
	            }
	            
	        }
	        
	        return false;
	    } 
	 
	 
	 
	 class ConnectThread extends Thread {
		    private final BluetoothSocket mmSocket;
		    private final BluetoothDevice mmDevice;
		 
		    public ConnectThread() {
		        // Use a temporary object that is later assigned to mmSocket,
		        // because mmSocket is final
		        BluetoothSocket tmp = null;
		        mmDevice = MAC;
		 
		        // Get a BluetoothSocket to connect with the given BluetoothDevice
		        try {
		            // MY_UUID is the app's UUID string, also used by the server code
		            tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
		        } catch (IOException e) { }
		        mmSocket = tmp;
		    }
		 
		    public void run() {
		        // Cancel discovery because it will slow down the connection
		       // mBluetoothAdapter.cancelDiscovery();
		        System.out.println("Client Thread started");
		        //System.out.println("DEVICE "+vec.firstElement());
		        System.out.println("DEVICE "+mmDevice.toString());
		        try {
		            // Connect the device through the socket. This will block
		            // until it succeeds or throws an exception
		            mmSocket.connect();
		            System.out.println("Connecting");
		        } catch (IOException connectException) {
		            // Unable to connect; close the socket and get out
		            try {
		                mmSocket.close();
		            } catch (IOException closeException) { }
		            return;
		        }
		        
		        	
		        
		        // Do work to manage the connection (in a separate thread)
		        ConnectedThread connect = new ConnectedThread(mmSocket);	        
		        //String message = "hello";
		        //System.out.println(hello.getBytes());- 1
		        byte[] byteString = sound.getBytes();
	            //byteString[byteString.length ] = 0;
		        connect.write(byteString);
		        System.out.println("message wrote");
		        //manageConnectedSocket(mmSocket);
		    }
		 
		    /** Will cancel an in-progress connection, and close the socket */
		    public void cancel() {
		        try {
		            mmSocket.close();
		        } catch (IOException e) { }
		    }
		}
}
