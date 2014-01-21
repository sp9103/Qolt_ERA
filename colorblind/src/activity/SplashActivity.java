package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.naubull2.colorblind.R;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		

		// Show splash for 1sec
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				Intent intent;
				intent = new Intent(SplashActivity.this, MainActivity.class);

				// we don't need to revisit splash screen	
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();

			}
		}, 1000);
	}
}
