package fiona.com.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SensingContext extends Activity{
	// Instance Variables
    private SensingContext mainActivity = null;
	TextView tv;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.textView2);
        mainActivity = this;
        
        

	}

}
