package activity;

import java.io.File;

import utility.TempSetting;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import com.naubull2.colorblind.R;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		

		// Show splash for 1sec
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
		}, 1000);
	}
}
