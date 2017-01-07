package fiona.com.android;

import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.widget.Button;

public class BluetoothServer {
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
	    //List<String> myArray = new ArrayList<String>();
	    String[] array;
	    //Vector<String> vec = new Vector<String>();
	    Vector<String> macVec = new Vector<String>();
	   // JSONObject json= new JSONObject();
	    String url = "http://192.168.1.2/TestPhp/test4.php";
	    
	
}
