package fiona.com.android;

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeLabel extends Activity {
	Intent intent;
	Button change;
	EditText edit;
	String[] result;
	String parameters;
	SendDataToServer sendData = new SendDataToServer();
	String serverResult;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	Activity activity;
	Runnable runnable = new ServerThread();
	Intent mainActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changelabel);
		Bundle b = getIntent().getExtras();

		change = (Button) findViewById(R.id.button1);
		edit = (EditText) findViewById(R.id.editText1);

		mainActivity = new Intent(this, SensingEnvironmentActivity.class);

		intent = new Intent(this, SendDataToServer.class);
		if (b != null) {
			result = b.getStringArray("details");
		}

		change.getBackground().setColorFilter(
				new LightingColorFilter(Color.BLUE, 1));

		change.setOnClickListener(

		new OnClickListener() {

			@Override
			public void onClick(View v) {
				String label;
				String user;
				String add;
				String date;
				String time;
				String visibility;
				String weekday;
				String dateTime;
				dateTime = result[1];

				add = result[3];
				user = result[4];
				String delims = "[ ]";
				String[] tokens = dateTime.split(delims);
				date = tokens[0];
				time = tokens[1];
				weekday = tokens[2];

				label = edit.getText().toString();
				visibility = "n";

				if (edit.getText().toString().equals("")) {

					AlertDialog dialog = blankEditTextDialogBox();
					dialog.show();
				} else {
					String pars = "[" + label + "][" + add + "][" + date + "]["
							+ time + "][" + visibility + "][" + user + "]["
							+ weekday + "]";
					setParameters(pars);
					Thread thread2 = new Thread(runnable);
					thread2.start();
					startActivity(mainActivity);

				}

			}

			private AlertDialog blankEditTextDialogBox() {
				// Builder to object to specify optional arguments
				AlertDialog detailsDialog = new AlertDialog.Builder(
						ChangeLabel.this)
						// sets title and message to be displayed by dialog
						.setTitle("Blank Text Field")
						.setMessage("A label must be supplied")
						// this activity is ended when yes is selected
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();

									}
								}).create(); // creates builder object and
												// returns dialogbox to
				// call method
				return detailsDialog;

			}

		});

	}

	class ServerThread implements Runnable {

		public void run() {

			String param = getParameters();
			String path = getFilename();
			serverResult = sendData.sendData(path, param);
			displayOperationResult(serverResult);

		}
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

	public void setParameters(String para) {
		this.parameters = para;
	}

	public String getParameters() {
		return parameters;
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

	private void resultToast(Context ctx, String con) {
		String label;
		if (con != null) {
			label = serverResult;

		} else {
			label = "Sorry an error occured during this operation, Please try again.";
		}
		Toast.makeText(ChangeLabel.this, label, Toast.LENGTH_LONG).show();
	}
}
