package fiona.com.android;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import fiona.com.android.SensingEnvironmentService.GetContextTask;
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
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class RecordingActivity extends Activity {
	Button button;

	public boolean recording;
	public int frequency;

	String stringFrequency;
	int currentVolume = 0;
	int sumVolume = 0;
	int average = 0;
	int timedAverage = 0;
	int averageVariance = 0;
	int previousVariance = 0;
	int previousAverage = 0;
	private AudioRecord recorder = null;
	private int bufferSize = 0;
	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder4";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private boolean isRecording = false;
	private Thread recordingThread = null;
	Intent intent;
	Intent showSoundWindow;
	int numCrossing;
	Context context;
	DataOutputStream output;
	GetContextTask asyncContext;
	String contextResult = null;
	ServerInterface servInterface = new ServerInterface();
	boolean processing = false;
	String formattedDate;
	String nameString;
	List<Address> name;
	String address;
	String contextString;
	String contextNotification;
	int bufferCount = 0;
	Runnable runnable = new ServerThread();
	SendDataToServer sendData = new SendDataToServer();
	Activity activity;
	String addressLoc;
	RadioButton myRadio1, myRadio2;
	RadioGroup radioGroup;
	String contextVisibility;
	String formattedLoc;
	double lat;
	double longt;
	String userName;
	String categoryType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordinglayout);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener ll = new mylocationlistener();
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);

		Spinner categorySpinner = (Spinner) findViewById(R.id.spinner1);
		final Spinner catTypeSpinner = (Spinner) findViewById(R.id.spinner2);
		String catArray[] = { "Inanimate", "Human", "Animal", "Weather" };
		String catTypeArray1[] = { "clap", "bang", "whistle", "bell" };
		String catTypeArray2[] = { "female", "male", "child", "laugh",
				"scream", "cry" };
		String catTypeArray3[] = { "bird", "dog", "cat", "cow" };
		String catTypeArray4[] = { "wind", "rain", "lightening", "thunder" };

		ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, catArray);
		spinnerArrayAdapter1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		
		categorySpinner.setAdapter(spinnerArrayAdapter1);

		final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, catTypeArray1);
		spinnerArrayAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		
		catTypeSpinner.setAdapter(spinnerArrayAdapter2);

		final ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, catTypeArray2);
		spinnerArrayAdapter3
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		
		catTypeSpinner.setAdapter(spinnerArrayAdapter3);

		final ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, catTypeArray3);
		spinnerArrayAdapter4
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 

		catTypeSpinner.setAdapter(spinnerArrayAdapter4);

		final ArrayAdapter<String> spinnerArrayAdapter5 = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, catTypeArray4);
		spinnerArrayAdapter5
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		catTypeSpinner.setAdapter(spinnerArrayAdapter5);

		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				if (position == 0) {
					catTypeSpinner.setAdapter(spinnerArrayAdapter2);
				} else if (position == 1) {
					catTypeSpinner.setAdapter(spinnerArrayAdapter3);
				} else if (position == 2) {
					catTypeSpinner.setAdapter(spinnerArrayAdapter4);
				} else {
					catTypeSpinner.setAdapter(spinnerArrayAdapter5);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		catTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				categoryType = parentView.getItemAtPosition(position)
						.toString();
				System.out.println(categoryType);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		button = (Button) findViewById(R.id.button1);
		button.getBackground().setColorFilter(
				new LightingColorFilter(Color.BLUE, 1));
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		myRadio1 = (RadioButton) findViewById(R.id.radio0);
		myRadio2 = (RadioButton) findViewById(R.id.radio1);
		myRadio1.setChecked(true);
		myRadio2.setChecked(false);

		button.setOnClickListener(

		new OnClickListener() {

			@Override
			public void onClick(View v) {

				contextString = categoryType;
				System.out.println(contextString);
				startRecording();

				button.setEnabled(false);

				if (myRadio1.isChecked()) {
					contextVisibility = "y";
				} else {
					contextVisibility = "n";
				}

			}

		});

		Bundle b = getIntent().getExtras();

		if (b != null) {

			userName = b.getString("User");
		}

	}

	public String getDateTime() {
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formattedDate = df.format(c.getTime());

		int weekday = c.get(Calendar.DAY_OF_WEEK);

		System.out.println(formattedDate);
		return formattedDate + " " + Integer.toString(weekday);

	}

	private AlertDialog createDialogBox() {
		// Builder to object to specify optional arguments
		AlertDialog detailsDialog = new AlertDialog.Builder(this)
				// sets title and message to be displayed by dialog
				.setTitle("Missing Information")
				.setMessage(
						"You must enter a label before you can begin recording! For example dog bark.")
				// this activity is ended when yes is selected
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				}).create(); // creates builder object and returns dialogbox to
		// call method
		return detailsDialog;

	}

	private AlertDialog locationDialogBox() {
		// Builder to object to specify optional arguments
		AlertDialog detailsDialog = new AlertDialog.Builder(this)
				// sets title and message to be displayed by dialog
				.setTitle("Invalid Location")
				.setMessage(
						"Sorry a valid location has not been detected. Please try recording again in a minute.")
				// this activity is ended when yes is selected
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				}).create(); // creates builder object and returns dialogbox to
		// call method
		return detailsDialog;

	}

	private AlertDialog playAudioDialogBox() {
		// Builder to object to specify optional arguments
		AlertDialog detailsDialog = new AlertDialog.Builder(this)
				// sets title and message to be displayed by dialog
				.setTitle("Audio Information")
				.setMessage("Do you want to play recorded audio?")
				// this activity is ended when yes is selected
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								playAudio();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						RecordingActivity.this.finish();

					}

				}).create();
		return detailsDialog;

	}

	private AlertDialog saveAudioDialogBox() {
		// Builder to object to specify optional arguments
		AlertDialog detailsDialog = new AlertDialog.Builder(this)
				// sets title and message to be displayed by dialog
				.setTitle("Audio Information")
				.setMessage("Save Audio?")
				// this activity is ended when yes is selected
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								Toast.makeText(RecordingActivity.this,
										"Recording Saved", Toast.LENGTH_LONG)
										.show();
								Thread thread2 = new Thread(runnable);
								thread2.start();
								RecordingActivity.this.finish();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						File file = new File(getFilename());
						file.delete();
						Toast.makeText(RecordingActivity.this,
								"Recording Erased", Toast.LENGTH_LONG).show();
						RecordingActivity.this.finish();
					}

				}).create();
		return detailsDialog;

	}

	public void playAudio() {
		MediaPlayer mp = new MediaPlayer();
		String file = getFilename();
		try {
			mp.setDataSource(file);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.start();

		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				AlertDialog dialog = saveAudioDialogBox();
				dialog.show();

			}
		});
	}

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		String audio = "recording";
		return (file.getAbsolutePath() + "/" + audio + AUDIO_RECORDER_FILE_EXT_WAV);
	}

	private String getTempFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

		if (tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

	public void startRecording() {
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, bufferSize);

		recorder.startRecording();

		isRecording = true;

		recordingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");

		recordingThread.start();
	}

	@SuppressWarnings("unchecked")
	private void writeAudioDataToFile() {
		byte data[] = new byte[bufferSize];
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis() + 5000;

		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int read = 0;

		if (null != os) {
			while (isRecording) {
				while (bufferCount < 20) {
					System.out.println("Buffersize: " + bufferCount);
					read = recorder.read(data, 0, bufferSize);

					if (AudioRecord.ERROR_INVALID_OPERATION != read) {
						try {

							os.write(data);

						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					bufferCount++;
				}

				finishRecording();

			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void stopRecording() {
		if (null != recorder) {
			isRecording = false;

			recorder.stop();
			recorder.release();

			recorder = null;
			recordingThread = null;
		}

		copyWaveFile(getTempFilename(), getFilename());
		deleteTempFile();

	}

	public void finishRecording() {
		stopRecording();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showToast(activity);
				AlertDialog dialog = playAudioDialogBox();
				dialog.show();
				button.setEnabled(true);
			}
		});

		Log.i("Test", "Closing Application");
	}

	public void displayOperationResult(final String con) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				resultToast(activity, con);
			}
		});

		Log.i("Test", "Operation Complete");
	}

	private void showToast(Context ctx) {
		Toast.makeText(RecordingActivity.this, "Recording Stopped",
				Toast.LENGTH_LONG).show();
	}

	private void resultToast(Context ctx, String con) {
		String label;
		if (con != null) {
			label = contextNotification;

		} else {
			label = "Sorry an error occured during this operation, Please try again.";
		}
		Toast.makeText(RecordingActivity.this, label, Toast.LENGTH_LONG).show();
	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());

		file.delete();
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	private class mylocationlistener implements LocationListener {

		Geocoder gCoder = new Geocoder(getApplicationContext(),
				Locale.getDefault());

		@Override
		public void onLocationChanged(Location location) {
			nameString = "Default";
			System.out.println("NameString " + nameString);

			lat = location.getLatitude();
			longt = location.getLongitude();

			Log.d("LOCATION CHANGED", location.getLatitude() + "");
			Log.d("LOCATION CHANGED", location.getLongitude() + "");

			try {
				name = gCoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);
				if (name.equals(null)) {
					nameString = "Location Name could not be detected BUT GPS coordinates are"
							+ lat + "," + longt;
				} else {
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
					if (add5 == null) {
						add5 = "Unspecified Locality";
					}

					System.out
							.println(" Feature Name " + add4 + " Sub Loc "
									+ add10 + " Sub Area " + add9
									+ " Address Area " + add1
									+ " Country Name " + add3
									+ " Country Code " + add2 + " Locality "
									+ add5 + " Postcode " + add11);
					address = add4 + "," + add10 + "," + add5 + "," + add9
							+ "," + add1 + "," + add3 + "," + add2;

					if (add10 == null) {
						add10 = "Unknown_Street";
					}
					if (add9 == null) {
						add9 = "Unknown_Locality";
					}
					if (add1 == null) {
						add1 = "Unknown_City";
					}
					if (add3 == null) {
						add3 = "Unknown_Country";
					}

					addressLoc = add10 + "," + add9 + "," + add1 + "," + add3;
					if (addressLoc != null) {
						formattedLoc = addressLoc.replace(" ", "_");
					}

					System.out.println("Name " + name);
					System.out.println("Address " + address);
				}
			} catch (IOException e) {
				System.out.println("Problem Acquiring Location name " + e);
				e.printStackTrace();
			}

			// }
			System.out.println("Address Location: " + addressLoc);
			System.out.println("Formatted Location: " + formattedLoc);

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

	class ServerThread implements Runnable {

		public void run() {
			System.out.println("In ServerThread()");
			String path = getFilename();
			String dateTime = getDateTime();
			String delims = "[ ]";
			String[] tokens = dateTime.split(delims);
			String date = tokens[0];
			String time = tokens[1];
			String weekdayString = tokens[2];
			String param = "[" + contextString + "][" + formattedLoc + "]["
					+ date + "][" + time + "][" + contextVisibility + "]["
					+ userName + "][" + weekdayString + "]";
			System.out.println("PARAMs: " + param);
			contextNotification = sendData.sendData(path, param);
			System.out.println("RESULT: " + contextNotification);
			displayOperationResult(contextNotification);
			System.out.println("ContextSent");

		}
	}
}
