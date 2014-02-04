package activity;

import java.io.File;

import libera.EraCore;
import utility.ImageProcessHelper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.naubull2.colorblind.R;

public class SplashActivity extends Activity {
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	
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

		setContentView(R.layout.activity_splash);
		mContext = this;
		
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		editor = pref.edit();
		
		new NativeTask().execute();
		
		/*// Show splash for 1sec
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
				File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ERA");
				if(!mediaStorageDir.exists())
					mediaStorageDir.mkdirs();
				
				Intent intent;
				// intent = new Intent(SplashActivity.this, MainActivity.class);
				intent = new Intent(SplashActivity.this, TempSetting.class);

				// we don't need to revisit splash screen	
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();

			}
		}, 1000);*/
	}
	
	private class NativeTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... arg) {
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ERA");
			if(!mediaStorageDir.exists())
				mediaStorageDir.mkdirs();
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
			
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			// we don't need to revisit	
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
		}
	}
}
