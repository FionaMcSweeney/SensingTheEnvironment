package fiona.com.android;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class ConnectedThread extends Thread {
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
		byte[] buffer = new byte[1024]; // buffer store for the stream
		int bytes = 0; // bytes returned from read()
		String message = null;

		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);

				System.out.println("InputStream: " + bytes);

				message = message + new String(buffer, 0, bytes);
				System.out.println("Message " + message);

				// handleUIRequest(message);

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
		} catch (IOException e) {
		}
	}

}
