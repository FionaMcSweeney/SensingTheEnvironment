package fiona.com.android;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BluetoothActivity extends Service{
		protected static final int REQUEST_ENABLE_BT = 0;
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BroadcastReceiver mReceiver;
		BluetoothDevice device;
		Vector<String> vec = new Vector<String>();
		Button button1;
		Button button2;
		Button button3;
		Button button4;
		Button button5;
		Button button6;
		String mac;
		String stringUUID = "85c2c650-dfd8-11e1-9b23-0800200c9a66";
		UUID uuid = UUID.fromString(stringUUID);	   
		String NAME = "Bluetooth";	   
		String[] array;
		Vector<String> macVec = new Vector<String>();
		String url = "http://192.168.1.2/TestPhp/test4.php";
		String message;
		String connection = null;
		Thread t2=new AcceptThread();
		@Override
		public void onCreate() {
		
		}

		
		@Override
		public void onStart(Intent intent, int startid) {
			
		}
		
	    
	    public void startAcceptActivity(){
	    	//Thread t2=new AcceptThread();
			t2.start();
	    }
	    public void stopAcceptActivity(){
	    	AcceptThread acceptT = new AcceptThread();
			acceptT.cancel();
	    }
	    
	  
	    public void connectToDevice(String msg, BluetoothDevice dev){
	    	this.message = msg;
	    	this.device = dev;
	    	Thread t1=new ConnectThread();
			t1.start();
	    }
	        
	        
	        protected void Stop()
	        {
	        	
	        	mBluetoothAdapter.cancelDiscovery();
	        	try{
	        		if(mReceiver != null){
	        	unregisterReceiver(mReceiver);
	        		}
	        	}catch(Exception e){
	        		System.out.println("Exception "+e);
	        	}
	          //  
	        }
	        
	        
	        public void onDestroy(){
	        //	mBluetoothAdapter.cancelDiscovery();
	        	
	        	super.onDestroy();
	                 
	        	
	        	try{
	        //		if(mReceiver != null){
	        	unregisterReceiver(mReceiver);
	        	//	}
	        	}catch(Exception e){
	        		System.out.println("Exception "+e);
	        	}
	        //	super.onDestroy();
	        }
	        
	        
	        
	        private class AcceptThread extends Thread {
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
	         
	            public void run() {
	                BluetoothSocket socket = null;
	                System.out.println("In Server Thread");
	                connection = "listening";
	              //  Toast.makeText(BluetoothTest2Activity.this, "Server Started", Toast.LENGTH_LONG).show();
	       		 
	                // Keep listening until exception occurs or a socket is returned
	                while (true) {
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
	         
	            /** Will cancel the listening socket, and cause the thread to finish */
	            public void cancel() {
	                try {
	                    mmServerSocket.close();
	                } catch (IOException e) { }
	            }
	        }
	        
	        private class ConnectedThread extends Thread {
	            private final BluetoothSocket mmSocket;
	            private final InputStream mmInStream;
	            private final OutputStream mmOutStream;
	            DataInputStream din;
	         
	            public ConnectedThread(BluetoothSocket socket) {
	                mmSocket = socket;
	                InputStream tmpIn = null;
	                OutputStream tmpOut = null;
	         
	                // Get the input and output streams, using temp objects because
	                // member streams are final
	                try {
	                    tmpIn = socket.getInputStream();
	                    tmpOut = socket.getOutputStream();
	                } catch (IOException e) { }
	         
	                mmInStream = tmpIn;
	                mmOutStream = tmpOut;
	                din = new DataInputStream(mmInStream);
	            }
	         
	            public void run() {
	            	System.out.println("In Connected Thread");
	            	System.out.println("Input: "+mmInStream);
	                byte[] buffer = new byte[1024];  // buffer store for the stream
	                int bytes = 0; // bytes returned from read()
	                String message = null;
	                
	                
	           
	                while (true) {
	                    try {
	                        // Read from the InputStream
	                        bytes = mmInStream.read(buffer);
	                        
	                        System.out.println("InputStream: "+bytes);
	                        
	                        
	                        message = message + new String(buffer, 0, bytes); 
	                        System.out.println("Message "+message);
	                        
	                        
	                   
	                    } catch (IOException e) {
	                        break;
	                    }
	                }
	            }
	         
	            /* Call this from the main activity to send data to the remote device */
	            public void write(byte[] bytes) {
	                try {
	                    mmOutStream.write(bytes);
	                    System.out.println("Wrote Bytes");
	                } catch (IOException e) {
	                	System.out.println("Exception");
	                }
	            }
	         
	            /* Call this from the main activity to shutdown the connection */
	            public void cancel() {
	                try {
	                    mmSocket.close();
	                } catch (IOException e) { }
	            }
	        }
	        
	        class ConnectThread extends Thread {
	    	    private final BluetoothSocket mmSocket;
	    	    private final BluetoothDevice mmDevice;
	    	 
	    	    public ConnectThread() {
	    	        // Use a temporary object that is later assigned to mmSocket,
	    	        // because mmSocket is final
	    	        BluetoothSocket tmp = null;
	    	        mmDevice = device;
	    	 
	    	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	    	        try {
	    	            // MY_UUID is the app's UUID string, also used by the server code
	    	            tmp = device.createRfcommSocketToServiceRecord(uuid);
	    	        } catch (IOException e) { }
	    	        mmSocket = tmp;
	    	    }
	    	 
	    	    public void run() {
	    	        // Cancel discovery because it will slow down the connection
	    	        mBluetoothAdapter.cancelDiscovery();
	    	        System.out.println("Client Thread started");
	    	        //System.out.println("DEVICE "+vec.firstElement());
	    	        //System.out.println("DEVICE "+device.toString());
	    	        try {
	    	            // Connect the device through the socket. This will block
	    	            // until it succeeds or throws an exception
	    	            mmSocket.connect();
	    	        } catch (IOException connectException) {
	    	            // Unable to connect; close the socket and get out
	    	            try {
	    	                mmSocket.close();
	    	            } catch (IOException closeException) { }
	    	            return;
	    	        }
	    	 
	    	        
	    	        // Do work to manage the connection (in a separate thread)
	    	        ConnectedThread connect = new ConnectedThread(mmSocket);
	    	        //String hello = "hello";
	    	        //System.out.println(hello.getBytes());- 1
	    	        byte[] byteString = message.getBytes();
	                byteString[byteString.length ] = 0;
	    	        connect.write(byteString);
	    	        //manageConnectedSocket(mmSocket);
	    	    }
	    	 
	    	    /** Will cancel an in-progress connection, and close the socket */
	    	    public void cancel() {
	    	        try {
	    	            mmSocket.close();
	    	        } catch (IOException e) { }
	    	    }
	    	}

			@Override
			public IBinder onBind(Intent arg0) {
				// TODO Auto-generated method stub
				return null;
			}
	    
	    
}
