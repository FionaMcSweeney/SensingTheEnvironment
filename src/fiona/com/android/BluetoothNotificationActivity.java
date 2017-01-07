package fiona.com.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
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

public class BluetoothNotificationActivity extends Activity{
	Button button;
	Intent intent;
	TextView t1;
	String result;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.btnotification);
        
        button = (Button) findViewById(R.id.button1);
        t1 = (Button) findViewById(R.id.textView1);
       
        intent = new Intent(this, BluetoothService.class);
        
        Bundle b = getIntent().getExtras();
       
        
		if(b != null){
			result = b.getString("result");
			t1.setText("A Context similar to "+result+" has been sent via bluetooth");
			
			
		}
		else { 	t1.setText("Someone is trying to pass a message via bluetooth");
				}
		
		
				
		button.setOnClickListener(
				
				new OnClickListener()
				{

					@Override
					public void onClick(View v) {
					}
					       			
				});
		
    }
}
