package fiona.com.android;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash_Screen extends Activity {
	Button login;
	//Button reg;
	Intent loginIntent;
	Intent regIntent;
	String userName;

	protected boolean active = true;// variables to identify is splash screen
									// displayed and for how long
	protected int splashTime = 5000;
	
	ImageView myImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);// set layout to splash screen
												// xml
		// set up textview variables for title of splash screen

        loginIntent = new Intent(this,SensingEnvironmentActivity.class);
        regIntent = new Intent(this,registerActivity.class);
       // login = (Button) findViewById(R.id.button1);
       // login.getBackground().setColorFilter(new LightingColorFilter(Color.RED, 1));//0xFFAA0000 ));
        
		// create image to be displayed on splash screen
		myImage = (ImageView) findViewById(R.id.imageView1);
		myImage.setImageResource(R.drawable.wave1);
		// create new thread to determine for how long screen should be
		// displayed
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;// create loop that puts thread to sleep
									// every second for 5 seconds
					while (active && (waited < splashTime)) {
						sleep(100);
						if (active) {
							waited += 100;// increment loop variable
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Error " + e);
				} finally {
					// end this activity, start selection screen activity and
					// stop thread
					
					
					File file = new File(Environment.getExternalStorageDirectory() + File.separator + "id.txt");
					
					if (file.exists()){
						try {
							StringBuilder text = new StringBuilder();
							BufferedReader br = new BufferedReader(new FileReader(file));
							String line = br.readLine();
							text.append(line);
							
							Bundle b = new Bundle();
							b.putSerializable("User", line);
							loginIntent.putExtras(b);
							startActivity(loginIntent);
							
							System.out.println("ID: "+line);
							br.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
							userName = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
						

						     Bundle b = new Bundle();
								b.putSerializable("User", userName);
								regIntent.putExtras(b);
								startActivity(regIntent);
					}
					
					finish();
					//stop();
				}
			}
		};
		splashTread.start();// starts thread

	}

	@Override
	// end this activity, start selection screen activity if screen is touched
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			active = false;
		}
		return true;
	}
}
