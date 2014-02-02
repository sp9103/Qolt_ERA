package utility;

import libera.EraCore;
import activity.MainActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.naubull2.colorblind.R;

public class TempSetting extends Activity{
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private EditText mTextbox;
	private Context mContext;
	
	/////////////////////////////////////////////////////////////////////////////
	// NO IDEA WHY BUT THIS IS NEEDED. OTHERWISE ERA LIBRARY WILL CRASH ON INIT
	private ImageProcessHelper mImageProcHelper = new ImageProcessHelper();
	/////////////////////////////////////////////////////////////////////////////
	
	// call native methods through era object here
	private EraCore era = new EraCore();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp_activity);
		
		mContext = this;
		
		Button btn = (Button)findViewById(R.id.button1);
		mTextbox = (EditText)findViewById(R.id.editText1);
		
		pref = getSharedPreferences("ERA", MODE_PRIVATE);
		editor = pref.edit();
		
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// save current calibration value
				String value = mTextbox.getText().toString();
				if(!value.isEmpty())
					editor.putFloat("era_calib", Float.parseFloat(value)).commit();
				
				new NativeTask().execute();
				
				Intent intent = new Intent(TempSetting.this, MainActivity.class);
				// we don't need to revisit	
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
				
			}
		});
	}
	
	private class NativeTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// Opens dialog on loading data file to external storage
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Processing");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... arg) {

			/**
			 * 
			 * put binary data creation code here 
			 * 
			 * (use EraCore object created at the class initialization)
			 * 
			 * 
			 */
			
			// Make binary Data file example - Image Correction example
			// if u want make DYSCHROMATOPSA Data file, insert factor & mode = 1
			// mode 0 : Image Correction
			// mode 1 : Image DYSCHROMATOPSA
			float native_factor = pref.getFloat("era_calib", (float) 0.4);
			era.MakeTreeFile(10, native_factor, "/sdcard/Pictures/ERA/ImageCorrectionData.bin", 0);
			
			return true;
		}
		@Override
		protected void onPostExecute(Boolean isDone) {
			pDialog.dismiss();
		}
	}
	
	

}
