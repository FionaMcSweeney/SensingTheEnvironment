package fiona.com.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
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
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;



public class SensingEnvironmentService extends Service implements LocationListener{
	private static final String TAG = "MyService";
	public boolean recording;  //variable to start or stop recording
	public int frequency; //the public variable that contains the frequency value "heard", it is updated continually while the thread is running.
	String stringFrequency;
	int currentVolume = 0;
	int sumVolume = 0;
	int average = 0;
	int timedAverage = 0;
	int averageVariance =0;
	int previousVariance =0;
	int previousAverage =0;
	private AudioRecord recorder = null;
	private int bufferSize = 0;
	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	boolean isRecording = false;
	private Thread recordingThread = null;
	Intent intent;
	Intent showSoundWindow;
	int numCrossing;
	Context context;
	DataOutputStream output; // output stream to target file
	GetContextTask asyncContext;
	String contextResult = null;
	ServerInterface servInterface = new ServerInterface();
	boolean processing = false;
	Runnable runnable = new ServerThread();
	String formattedDate;
	String nameString;
	List<Address> name;
	String address;
	LocationManager lm;
	LocationListener ll;
	String netProvider;
	String gpsProvider;
	String ns;// = Context.NOTIFICATION_SERVICE;
	NotificationManager mNotificationManager; //= (NotificationManager) getSystemService(ns);
	int NOTIF_ID= 1;
	boolean serviceStatus;
	SendDataToServer sendData = new SendDataToServer();
	String contextNotification;
	String addressLoc;
	String formattedLoc;
	double lat;
	double longt;
	String userName;
	SendDataToServer server = new SendDataToServer();
	String notificationAdd;
	String locResult;
	Bundle changeLabel;
	
	
 private class mylocationlistener implements LocationListener {
    	
    	Geocoder gCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
    	
    @Override
    public void onLocationChanged(Location location) {
    
    	nameString = "Default";
    	System.out.println("NameString "+nameString);
        if (location != null) {
        	
        	lat = location.getLatitude();
        	longt = location.getLongitude();
        	
        	System.out.println("LAT "+lat+" "+"LONG "+longt);
        	
        Log.d("LOCATION CHANGED", location.getLatitude() + "");
        Log.d("LOCATION CHANGED", location.getLongitude() + "");
        
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
				formattedLoc = addressLoc.replace(" ", "_");
				System.out.println("Name "+name);
				System.out.println("Address "+address);
			}
		} catch (IOException e) {
			System.out.println("Problem Acquiring Location name "+ e);
			e.printStackTrace();
		}
		
				
        }
        	
    }
    
   
    private void If(boolean b) {
	
	}
	@Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    }

 
 public String getDateTime(){
 	Calendar c = Calendar.getInstance();
     System.out.println("Current time => "+c.getTime());

     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     formattedDate = df.format(c.getTime());
	
     int weekday = c.get(Calendar.DAY_OF_WEEK);
     
     System.out.println(formattedDate);
		return formattedDate+" "+Integer.toString(weekday);
 	
 }
	void showNotification() {
	    // look up the notification manager service
		 ns = Context.NOTIFICATION_SERVICE;
		 mNotificationManager = (NotificationManager) getSystemService(ns);
		 
		 
		 
		int icon = R.drawable.waveicon;
		CharSequence tickerText = "Notification";
		long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText, when);
	    
		Context context = getApplicationContext();
		CharSequence contentTitle = "*Notification*";
		CharSequence contentText = "An Environment Sound has been Detected!";
		
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		//notification.defaults |= Notification.FLAG_AUTO_CANCEL;
		
		String dateTime = getDateTime();
		
		
		String [] result = {contextNotification, dateTime, notificationAdd, formattedLoc, userName};
		
		
		
		Intent notificationIntent = new Intent(this, SoundDetectionActivity.class);
		//Intent notificationIntent = new Intent(this, SensingEnvironmentActivity.class);
		
		Bundle b = new Bundle();
    	b.putStringArray("result", result);
    	notificationIntent.putExtras(b);
    	notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | Notification.FLAG_AUTO_CANCEL);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		 
		//((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(NOTIF_ID, notification);
		mNotificationManager.notify(NOTIF_ID, notification);
		
		this.stopSelf();
		
	    }
	public void CancelNotification(int notifyId) {
		
		 mNotificationManager.cancel(notifyId);
	}
	public void soundDetected(){
		String dateTime = getDateTime();
		String [] result = {"Loud Noise", dateTime, address};
		
		Intent dialogIntent = new Intent(getBaseContext(), SoundDetectionActivity.class);
		Bundle b = new Bundle();
    	b.putStringArray("result", result);
    	dialogIntent.putExtras(b);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(dialogIntent);
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		//Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
	    crit.setPowerRequirement(Criteria.POWER_LOW);
	    crit.setAccuracy(Criteria.ACCURACY_COARSE);
	    netProvider = lm.getBestProvider(crit, false);
	    Criteria crit2 = new Criteria();
	    crit2.setAccuracy(Criteria.ACCURACY_FINE);
	    gpsProvider = lm.getBestProvider(crit2, false);
	    ll = new mylocationlistener();
	    lm.requestLocationUpdates(netProvider,0, 0, ll);
        lm.requestLocationUpdates(gpsProvider,0, 0, ll);
	    //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
	    serviceStatus = true;
		
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Listener Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		stopRecording();
		if(name != null){
        	lm.removeUpdates(ll);
        	lm = null;
        	ll = null;
        }
		serviceStatus = false;
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Listener Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		userName = intent.getStringExtra("User");
		startRecording();
	}
	
	private String getFilename(){
	    String filepath = Environment.getExternalStorageDirectory().getPath();
	    File file = new File(filepath,AUDIO_RECORDER_FOLDER);
	    
	    if(!file.exists()){
	            file.mkdirs();
	    }
	    
	   // return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
	    
	    String audio = "recording";
	    return (file.getAbsolutePath() + "/" + audio + AUDIO_RECORDER_FILE_EXT_WAV);
	}
	
	
	private String getTempFilename(){
	    String filepath = Environment.getExternalStorageDirectory().getPath();
	    File file = new File(filepath,AUDIO_RECORDER_FOLDER);
	    
	    if(!file.exists()){
	            file.mkdirs();
	    }
	    
	    File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
	    
	    if(tempFile.exists())
	            tempFile.delete();
	    
	    return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}
	public void startRecording(){
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING)*7;
	    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
	                                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
	    System.out.println("BufferSize"+bufferSize);
	    recorder.startRecording();
	    
	    isRecording = true;
	    
	    recordingThread = new Thread(new Runnable() {
	            
	            @Override
	            public void run() {
	                    writeAudioDataToFile();
	            }
	    },"AudioRecorder Thread");
	    
	    recordingThread.start();
	}
	@SuppressWarnings("unchecked")
	private void writeAudioDataToFile(){
	    byte data[] = new byte[bufferSize];
	    
	    float tempFloatBuffer[] = new float[3];
	    int tempIndex           = 0;

	    String filename = getTempFilename();
	    FileOutputStream os = null;
	  
	    int count = 0;
	   
	    
	    try {
	            os = new FileOutputStream(filename);
	    } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    }
	    
	    int read = 0;
	    int read1 = 0;
	    int read2 = 0;
	    
	    if(null != os){
	            while(isRecording){
	            	float temp = 0.0f;
	            	float totalAbsValue = 0.0f;
	                short sample        = 0; 
	            	
	            double sum  = 0;
	                    read = recorder.read(data, 0, bufferSize);
	                   
	                    
	                    for( int i=0; i<bufferSize; i+=2 ) 
	                    {
	                        sample = (short)( (data[i]) | data[i + 1] << 8 );
	                        totalAbsValue += Math.abs( sample ) / (read/2);
	                    }
	                    
	                    
	                    tempFloatBuffer[tempIndex%3] = totalAbsValue;
	                    
	                    for( int i=0; i<3; ++i )
	                        temp += tempFloatBuffer[i];
	                    
	                    System.out.println("TEMP: "+temp);
	                    

	                    for (int i = 0; i < read; i++) {
	                		
	                		sum += data [i] * data [i];
	                	}
	                	if (read > 0) {
	                		final double amplitude = sum / read;
	                		currentVolume = ((int) Math.sqrt(amplitude));
	                	}
	              System.out.println("Current Volume: "+currentVolume);
	                    count++;
	                    System.out.println("Volume: "+currentVolume); 
	                    System.out.println("Previous Volume: "+previousAverage);
	                    System.out.println("Count: "+count);
	                    System.out.println("FORMATTEDLOC: "+formattedLoc);
	                    if(count >= 10){
	                    	if((currentVolume > previousAverage +5 || temp > 3000) ){//&& (formattedLoc != null)){
	                    	//if(temp > 70000){
	                    		System.out.println("LOUD NOISE");  
	                    	
	                    			if(AudioRecord.ERROR_INVALID_OPERATION != read){
	                            try {
	                            	
	                               os.write(data);
	                                
	                                
	                            } catch (IOException e) {
	                                    e.printStackTrace();
	                            }
	                            System.out.println("Stopping Recording!!!"); 
	                            copyWaveFile(getTempFilename(),getFilename());
	                            deleteTempFile();
	                            //soundDetected();
	                            Thread thread2 = new Thread(runnable);
		                          thread2.start();
	                            //showNotification();
	                            String noise = "noise!!";
	                            //GetContextTask context; 
	                           // BluetoothActivity bluetooth = new BluetoothActivity();
	                           // bluetooth.stopAcceptActivity();
	                            
	                            stopRecording();
	                            
	                           // this.stopSelf();
	                          //  stopRecording();
	                      
	                    			}		
	                    	}
	                    }
	                    previousAverage = currentVolume;
	            }
	            
	            try {
	                    os.close();
	            } catch (IOException e) {
	                    e.printStackTrace();
	            }
	    }
	}
	

	public void stopRecording(){
	    if(null != recorder){
	            isRecording = false;
	            
	            recorder.stop();
	            recorder.release();
	            
	            recorder = null;
	            recordingThread = null;
	    }
	    
	   //onDestroy();
	    
	  //  copyWaveFile(getTempFilename(),getFilename());
	  //  deleteTempFile();
	}

	private void deleteTempFile() {
	    File file = new File(getTempFilename());
	    
	    file.delete();
	}
	private void copyWaveFile(String inFilename,String outFilename){
	    FileInputStream in = null;
	    FileOutputStream out = null;
	    long totalAudioLen = 0;
	    long totalDataLen = totalAudioLen + 36;
	    long longSampleRate = RECORDER_SAMPLERATE;
	    int channels = 2;
	    long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
	    
	    byte[] data = new byte[bufferSize];
	    
	    try {
	            in = new FileInputStream(inFilename);
	            out = new FileOutputStream(outFilename);
	            totalAudioLen = in.getChannel().size();
	            totalDataLen = totalAudioLen + 36;
	            
	           // AppLog.logString("File size: " + totalDataLen);
	            
	            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
	                            longSampleRate, channels, byteRate);
	            
	            while(in.read(data) != -1){
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

	private void WriteWaveFileHeader(
	            FileOutputStream out, long totalAudioLen,
	            long totalDataLen, long longSampleRate, int channels,
	            long byteRate) throws IOException {
	    
	    byte[] header = new byte[44];
	    
	    header[0] = 'R';  // RIFF/WAVE header
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
	    header[12] = 'f';  // 'fmt ' chunk
	    header[13] = 'm';
	    header[14] = 't';
	    header[15] = ' ';
	    header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
	    header[17] = 0;
	    header[18] = 0;
	    header[19] = 0;
	    header[20] = 1;  // format = 1
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
	    header[32] = (byte) (2 * 16 / 8);  // block align
	    header[33] = 0;
	    header[34] = RECORDER_BPP;  // bits per sample
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


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
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
	
	protected class GetContextTask extends AsyncTask<Object, Object, Object> {
		 
	    /**
	     * Let's make the http request and return the result as a String.
	     */
	    protected String doInBackground(Object... arg0) {
	        return ServerInterface.getContext();
	    }
	 
	    /**
	     * Parse the String result, and create a new array adapter for the list
	     * view.
	     */
	    protected void onPostExecute(Object objResult) {
            // check to make sure we're dealing with a string
            if(objResult != null && objResult instanceof String) {                          
                    String result = (String) objResult;
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
    }
	 
	}
	
	class ServerThread implements Runnable {

	    public void run() {
	  
	    	
	    	String path = getFilename();
	    	String dateTime = getDateTime();
	    	String delims = "[ ]";
	    	String[] tokens = dateTime.split(delims);
	    	String date = tokens[0];
	    	String time = tokens[1];
	    	String weekdayString = tokens[2];
	    	System.out.println("DATE: "+date+"TIME: "+time);
	    	String param = "[nothing]["+formattedLoc+"]["+date+"]["+time+"]["+"X]["+userName+"]["+weekdayString+"]";
	    	contextNotification = sendData.sendData(path,param);//"nothing",addressLoc,date,time, null);
	    	System.out.println(param);
	    	System.out.println("Context Notification: "+contextNotification);
	    	locResult = sendData.getLocationFromServer();
	    	System.out.println(locResult);
			if (locResult.equals("GPS")){
				notificationAdd = formattedLoc;
				System.out.println("gps location is true");			
			}
			else{
				notificationAdd = locResult;
				System.out.println("server location is true");
			}
	    	showNotification();
	    	//SensingEnvironmentService.this.stopSelf();
	   	 
	    }
	}

}
