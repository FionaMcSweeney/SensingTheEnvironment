package fiona.com.android;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;
import fiona.com.android.BluetoothActivity.ConnectThread;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothService extends Service {
	protected static final int REQUEST_ENABLE_BT = 0;
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BroadcastReceiver mReceiver; BluetoothDevice device;	
	Vector<String> vec = new Vector<String>();
	Button button1, button2, button3, button4, button5, button6;
	String mac,stringUUID = "c0c8351a-7cd0-4b74-9b8b-28a3b93d4b77";
	UUID uuid = UUID.fromString(stringUUID);
	String NAME = "SensingEnvironment",message; String[] array;	
	Vector<String> macVec = new Vector<String>();
	String url = "http://192.168.1.2/TestPhp/test4.php"; BluetoothDevice devMAC;	
	boolean connectionAvail;UIHandler uiHandler;	
	String receivedMessage = null,ns;
	NotificationManager mNotificationManager; int NOTIF_ID = 1;	
	Context context;
	private static final int SUCCESS = 0;
	private static final int FAILURE = 1;
	Intent sensingIntent,notificationIntent;
	final static String ACTION = "NotifyServiceAction";
	final static String STOP_SERVICE = "";
	final static int RQS_STOP_SERVICE = 1;
	Toast toast,btToast; TextView btText;
	private Handler mHandler;
	private Handler btHandler;	
	private static final int MY_NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	private Notification myNotification;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		setAvailConnection(false);
		mHandler = new Handler();
	}
	@SuppressWarnings("null")
	@Override
	public void onDestroy() {
		try {
			if (mReceiver != null) {
				unregisterReceiver(mReceiver);
			}
			stopServerSocket();
		} catch (Exception e) {
			System.out.println("Exception " + e);
		}
	}

	@Override
	public void onStart(Intent intent, int startid) {
		ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
		context = BluetoothService.this;
		notificationIntent = new Intent(context,BluetoothNotificationActivity.class);
		sensingIntent = new Intent(this, SensingEnvironmentActivity.class);
		Toast.makeText(this, "Bluetooth started", Toast.LENGTH_LONG).show();
		HandlerThread uiThread = new HandlerThread("UIHandler");
		uiThread.start();
		uiHandler = new UIHandler(uiThread.getLooper());
		Context context = getApplicationContext();
		CharSequence text = "Hello toast!";
		int duration = Toast.LENGTH_LONG;
		toast = Toast.makeText(context, text, duration);
	}
	public void setAvailConnection(boolean conn) {
		this.connectionAvail = conn;
	}
	public boolean getAvailConnection() {
		return connectionAvail;
	}
	public void startAcceptThread(Handler handler, TextView text) {
		this.btHandler = handler;
		this.btText = text;
		Thread t2 = new AcceptThread();
		t2.start();
	}
	public void connectToDevice(String msg, BluetoothDevice dev) {
		this.message = msg;
		this.devMAC = dev;
		Thread t1 = new ConnectThread();
		t1.start();
	}
	//bluetooth server class 
	class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;
		BluetoothSocket socket;
		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			try {
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
						NAME, uuid);
			} catch (IOException e) {
			}
			mmServerSocket = tmp;
		}
		public void run() {
			socket = null;
			setAvailConnection(true);
			while (true) {
				try {
					if (mmServerSocket != null)
						socket = mmServerSocket.accept();
				} catch (IOException e) {					
					break;
				}
				if (socket != null) {					
					ConnectedThread connect = new ConnectedThread(socket);
					connect.start();
					try {
						mmServerSocket.close();
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		public void stopSocket() {
			try {
				if (mmServerSocket != null)
					mmServerSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//disbles bluetooth
	public void stopBluetooth() {
		if (!mBluetoothAdapter.isEnabled()) {
		} else {
			mBluetoothAdapter.disable();
		}
	}
	public void stopServerSocket() {
		AcceptThread accept = new AcceptThread();
		accept.stopSocket();
	}
	//client-server connection class 
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		DataInputStream din;
		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
			din = new DataInputStream(mmInStream);
		}

		public void run() {
			System.out.println("In Connected Thread");
			System.out.println("Input: " + mmInStream);
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while (true) {
				try {
					bytes = mmInStream.read(buffer);
receivedMessage = new String(buffer, 0, bytes);
					System.out.println("Message " + receivedMessage);
					byte[] byteString = receivedMessage.getBytes();
					byteString[byteString.length - 1] = 0;
					mmOutStream.write(byteString);
					btHandler.post(new DisplayText(receivedMessage));
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
				// createToast();
			} catch (IOException e) {
			}
		}		
	}

	public void createToast() {
		getApplication().startActivity(sensingIntent);
	}

	private class DisplayText implements Runnable {
		private String mText;

		public DisplayText(String text) {
			mText = text;
		}

		public void run() {
			btText.setText("Bluetooth Message! " + mText + " sound detected.");
		}
	}
	protected void onHandleIntent(Intent intent) {
	}
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == SUCCESS) {
				Toast.makeText(
						BluetoothService.this,
						"MESSAGE! Detected Sound sent via bluetooth: "
								+ receivedMessage, Toast.LENGTH_LONG).show(); // the
																				// data
																				// you
																				// calculated
																				// from
																				// your
																				// thread
																				// can
																				// now
																				// be
																				// shown
																				// in
																				// one
																				// of
																				// your
																				// views.
			} else if (msg.what == FAILURE) {
				Toast.makeText(getApplicationContext(),
						"MESSAGE! Unable to receive message", Toast.LENGTH_LONG)
						.show();// could be your toasts or any other error
								// handling...
			}

		}
	};

	public final class UIHandler extends Handler {
		public static final int DISPLAY_UI_TOAST = 0;
		public static final int DISPLAY_UI_DIALOG = 1;

		public UIHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UIHandler.DISPLAY_UI_TOAST: {

				Toast t = Toast.makeText(BluetoothService.this,
						(String) msg.obj, Toast.LENGTH_LONG);
				t.show();
			}
			case UIHandler.DISPLAY_UI_DIALOG:
				// TBD
			default:
				break;
			}
		}
	}

	protected void handleUIRequest(String message) {
		Message msg = uiHandler.obtainMessage(UIHandler.DISPLAY_UI_TOAST);
		msg.obj = message;
		uiHandler.sendMessage(msg);
	}

	class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread() {

			BluetoothSocket tmp = null;
			mmDevice = devMAC;

			try {

				tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);

			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {

			System.out.println("Client Thread started");

			System.out.println("DEVICE " + mmDevice.toString());
			try {

				mmSocket.connect();
				System.out.println("Connecting");
			} catch (IOException connectException) {

				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

			ConnectedThread connect = new ConnectedThread(mmSocket);

			byte[] byteString = message.getBytes();

			connect.write(byteString);
			System.out.println("message wrote");

		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	void showNotification() {

		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Notification";
		long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText,
				when);

		CharSequence contentTitle = "*Notification*";
		CharSequence contentText = "Change in Environment Context has been Detected!";

		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		Bundle b = new Bundle();
		b.putString("result", receivedMessage);
		notificationIntent.putExtras(b);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
						| Notification.FLAG_AUTO_CANCEL);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(NOTIF_ID, notification);

		this.stopSelf();

	}

	public void CancelNotification(int notifyId) {

		mNotificationManager.cancel(notifyId);
	}

}
