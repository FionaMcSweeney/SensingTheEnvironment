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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class MainMenuActivity extends Activity {
	Button login;
	Intent loginIntent;
	Intent regIntent;
	String userName;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	        
        setContentView(R.layout.mainmenu);
        
        
        loginIntent = new Intent(this,SensingEnvironmentActivity.class);
        regIntent = new Intent(this,registerActivity.class);
        login = (Button) findViewById(R.id.button1);
        login.getBackground().setColorFilter(new LightingColorFilter(Color.RED, 1));
        login.setOnClickListener(
        		
        		new OnClickListener()
        		{

					@Override
					public void onClick(View v) {
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
						
					}
        			        			
        			
        		});
        
    }
}
