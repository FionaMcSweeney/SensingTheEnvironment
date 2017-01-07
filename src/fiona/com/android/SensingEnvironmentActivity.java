package fiona.com.android;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SensingEnvironmentActivity extends Activity {
	protected static final int SUCCESS = 0;
	Button button;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	//TextView tv;
	String name;
	Intent intent;
	Intent intent2;
	Intent intent3;
	Intent intent4;
	Intent btService;
	String userName;
	CheckBox btShare;
	SensingEnvironmentService service;
	BluetoothActivity bluetooth = new BluetoothActivity();
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	String stringUUID = "a7164340-e938-11e1-aff1-0800200c9a66";
	UUID uuid = UUID.fromString(stringUUID);	   
	String NAME = "SensingEnvironment";
	String connection;
	Handler handler;
	Toast btToast;
	boolean btEnabled;
	TextView btText;
	Intent intentToStart;
	Intent serviceToStop;
	BluetoothService bt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button.getBackground().setColorFilter(new LightingColorFilter(Color.MAGENTA, 1));//0xFFAA0000 ));
        button2.getBackground().setColorFilter(new LightingColorFilter(Color.GRAY, 1));//0xFFAA0000 ));
        button3.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x000000FF));//0xFFAA0000 ));
        button4.getBackground().setColorFilter(new LightingColorFilter(Color.LTGRAY,1 ));//0xFFAA0000 ));
        button5.getBackground().setColorFilter(new LightingColorFilter(Color.BLUE, 1));//0xFFAA0000 ));
        
       // tv = (TextView) findViewById(R.id.textView1);
        btText = (TextView) findViewById(R.id.btText);
        //btShare = (CheckBox) findViewById(R.id.checkBox1);
        intent = new Intent(this, SensingEnvironmentService.class);
        intent2 = new Intent(SensingEnvironmentActivity.this,RecordingActivity.class);
        intent3 = new Intent(SensingEnvironmentActivity.this,MainMenuActivity.class);
        intent4 = new Intent(SensingEnvironmentActivity.this,TrendActivity.class);
        btService = new Intent(SensingEnvironmentActivity.this,BluetoothService.class);
        Bundle b = getIntent().getExtras();
       // btShare.setChecked(false);
         bt = new BluetoothService();
        
        connection = null;
		if (b != null) {
			
			userName = b.getString("User");
		}
		
		Bundle id = new Bundle();
		id.putSerializable("User", userName);
		intent.putExtras(id);
		intent2.putExtras(id);
		
		
		
		
		
		
        //button3.setVisibility(View.INVISIBLE);
        button.setOnClickListener(
        		
        		new OnClickListener()
        		{

					@Override
					public void onClick(View v) {
					if(isMyServiceRunning()){
						Toast.makeText(SensingEnvironmentActivity.this, "Listener is Active for user:"+userName, Toast.LENGTH_LONG).show();
			
					}
					else if(isMyBluetoothServiceRunning()){
						System.out.println("Service Running!!!");
						AlertDialog dialog = createbtDialogBox();
						dialog.show();
					}
				/*	else if(connection!=null){
						Toast.makeText(SensingEnvironmentActivity.this, "Device is now listening for Bluetooth Alerts!", Toast.LENGTH_LONG).show();
					}*/
					else{
					/*	if(btShare.isChecked()){
			    		Intent discoverableIntent = new
						Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
						discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
						startActivity(discoverableIntent);
						if(!thread.isAlive()){
						thread.start();
						}
						//startService(btService);
						//bluetooth.startAcceptActivity();						
						
						}
						else{*/
						startService(intent);
						Log.i("Test", "Recording Started");
						}
						
					}
        			        			
        			
        		});
        button5.setOnClickListener(
        		
        		new OnClickListener()
        		{

					@Override
					public void onClick(View v) {
					if(isMyServiceRunning()){
						System.out.println("Service Running!!!");
						AlertDialog dialog = startbtDialogBox();
						dialog.show();
					}
					else if(isMyBluetoothServiceRunning()){
						Toast.makeText(SensingEnvironmentActivity.this, "Bluetooth service is Active", Toast.LENGTH_LONG).show();
			
					}
				
					else{
					
						startBluetooth();
						 /* 	if (mBluetoothAdapter.getScanMode() !=
				                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
				            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				            startActivity(discoverableIntent);
				        }
				     Intent discoverableIntent = new
								Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
								discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000);
								startActivity(discoverableIntent);
						
						startService(btService);
						bt.startAcceptThread(btHandler,btText);
						Log.i("Test", "Recording Started");*/
						/*
						Thread t2=new AcceptThread();
						//ConnectThread connectT = new ConnectThread();
						t2.start();*/
						}
						
					}
        			        			
        			
        		});
        
button2.setOnClickListener(
        		
        		new OnClickListener()
        		{

					@Override
					public void onClick(View v) {

						if(isMyServiceRunning()){
							System.out.println("Service Running!!!");
							AlertDialog dialog = createDialogBox();
							dialog.show();
						}
						else if(isMyBluetoothServiceRunning()){
							System.out.println("Service Running!!!");
							AlertDialog dialog = stopbtDialogBox();
							dialog.show();
						}
					/*	else if(connection !=null){
							AlertDialog dialog = btcreateDialogBox();
							dialog.show();
							btShare.setChecked(false);
						
							Log.i("Test", "Closing Application");
						}*/
						else{
							startActivity(intent2);
							Log.i("Test", "Recording Started");
						}	
						
						
						
					}
        			       			
        		});
button3.setOnClickListener(
		
		new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				if(isMyServiceRunning()){
					stopService(intent);				
					Log.i("Test", "Closing Application");
				}
				else if(isMyBluetoothServiceRunning()){
					bt.stopServerSocket();
					bt.stopBluetooth();
					stopService(btService);		
					//BluetoothService bt = new BluetoothService();
					//bt.stopServerSocket();
					Log.i("Test", "Closing Application");
				}
			/*	else if(connection != null){
					//bluetooth.stopAcceptActivity();
					//stopService(btService);
					thread.cancel();
					connection = null;
					btShare.setChecked(false);
				
					Log.i("Test", "Closing Application");
				}*/
				else{
					Toast.makeText(SensingEnvironmentActivity.this, "No services are active", Toast.LENGTH_LONG).show();
				}
				
				
			}
			       			
		});
button4.setOnClickListener(
		
		new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				if(isMyServiceRunning()){
					stopService(intent);
					startActivity(intent4);
					Log.i("Test", "Closing Application");
				}
				else if(isMyBluetoothServiceRunning()){
					stopService(btService);	
					startActivity(intent4);
					Log.i("Test", "Closing Application");
				}
			/*	else if(connection != null){
					thread.cancel();
					connection = null;
					btShare.setChecked(false);
				
					Log.i("Test", "Closing Application");
				}*/
				else{
					startActivity(intent4);
				}
				
				
			}
			       			
		});


    }
    
    public void startBluetooth(){
    	if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000);
            startActivity(discoverableIntent);
        }
    
		startService(btService);
		bt.startAcceptThread(btHandler,btText);
    }
    private AlertDialog createDialogBox() {
 		// Builder to object to specify optional arguments
 		AlertDialog detailsDialog = new AlertDialog.Builder(this)
 				// sets title and message to be displayed by dialog
 				.setTitle("Service Information")
 				.setMessage("You cannot perform this operation as the Environment Listener is running! " +
 						"Do you want to stop this service and start the recorder?")
 				// this activity is ended when yes is selected
 				.setPositiveButton("Yes",
 						new DialogInterface.OnClickListener() {

 							@Override
 							public void onClick(DialogInterface dialog,
 									int which) {
 								stopService(intent);
 								startActivity(intent2);
 								dialog.cancel();

 							}
 						})
 						.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}

				}).create();  // creates builder object and returns dialogbox to
 								// call method
 		return detailsDialog;

 	}
    
    private AlertDialog stopbtDialogBox() {
 		// Builder to object to specify optional arguments
 		AlertDialog detailsDialog = new AlertDialog.Builder(this)
 				// sets title and message to be displayed by dialog
 				.setTitle("Service Information")
 				.setMessage("You cannot perform this operation as the Bluetooth Service is running! " +
 						"Do you want to stop this service and start the recorder?")
 				// this activity is ended when yes is selected
 				.setPositiveButton("Yes",
 						new DialogInterface.OnClickListener() {

 							@Override
 							public void onClick(DialogInterface dialog,
 									int which) {
 								stopService(btService);
 								startActivity(intent2);
 								dialog.cancel();

 							}
 						})
 						.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}

				}).create();  // creates builder object and returns dialogbox to
 								// call method
 		return detailsDialog;

 	}
    
    private AlertDialog createbtDialogBox() {
 		// Builder to object to specify optional arguments
 		AlertDialog detailsDialog = new AlertDialog.Builder(this)
 				// sets title and message to be displayed by dialog
 				.setTitle("Service Information")
 				.setMessage("You cannot perform this operation as the Bluetooth Service is running! " +
 						"Do you want to stop this service and start the recorder?")
 				// this activity is ended when yes is selected
 				.setPositiveButton("Yes",
 						new DialogInterface.OnClickListener() {

 							@Override
 							public void onClick(DialogInterface dialog,
 									int which) {
 								stopService(btService);
 								startService(intent);
 								dialog.cancel();

 							}
 						})
 						.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}

				}).create();  // creates builder object and returns dialogbox to
 								// call method
 		return detailsDialog;

 	}
    
    private AlertDialog startbtDialogBox() {
 		// Builder to object to specify optional arguments
 		AlertDialog detailsDialog = new AlertDialog.Builder(this)
 				// sets title and message to be displayed by dialog
 				.setTitle("Service Information")
 				.setMessage("You cannot perform this operation as the Environment Listener is running! " +
 						"Do you want to stop this service and start the recorder?")
 				// this activity is ended when yes is selected
 				.setPositiveButton("Yes",
 						new DialogInterface.OnClickListener() {

 							@Override
 							public void onClick(DialogInterface dialog,
 									int which) {
 								stopService(intent);
 								startBluetooth();
 								//startService(btService);
 								dialog.cancel();

 							}
 						})
 						.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}

				}).create();  // creates builder object and returns dialogbox to
 								// call method
 		return detailsDialog;

 	}
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("fiona.com.android.SensingEnvironmentService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
/*    
    class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        
        public AcceptThread() {
        	
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }
     
        public void stopServerSocket() {
			// TODO Auto-generated method stub
			
		}

		public void run() {
            BluetoothSocket socket = null;
            System.out.println("In Server Thread");
          //  Toast.makeText(BluetoothTest2Activity.this, "Server Started", Toast.LENGTH_LONG).show();
   		 
            // Keep listening until exception occurs or a socket is returned
          // setAvailConnection(true);
            while (true) {
            	//System.out.println(getAvailConnection());
                try {
                    socket = mmServerSocket.accept();
                    System.out.println("socket connection");
                } catch (IOException e) {
                	System.out.println("Exception");
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                	System.out.println("Connection made");
                    // Do work to manage the connection (in a separate thread)
                	ConnectedThread connect = new ConnectedThread(socket);
                	connect.start();
                   // manageConnectedSocket(socket);
                    try {
						mmServerSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                }
                else{
                	System.out.println("Socket is null");
                }
            }
        }
     

	}
    */
    final Handler btHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		byte[] readBuf = (byte[]) msg.obj;
            // construct a string from the valid bytes in the buffer
            String readMessage = new String(readBuf, 0, msg.arg1); 
            btText.setText(readMessage);
            //Toast.makeText(SensingEnvironmentActivity.this, "MESSAGE! "+readMessage, Toast.LENGTH_LONG);//could be your toasts or any other error handling...
            
    			
    	}
    	};
   
}