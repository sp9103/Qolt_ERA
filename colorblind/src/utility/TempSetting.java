package utility;

import libera.EraCore;
import activity.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.naubull2.colorblind.R;

public class TempSetting extends Activity{
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private EditText mTextbox;
	
	// call native methods through era object here
	private EraCore era = new EraCore();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp_activity);
		
		Button btn = (Button)findViewById(R.id.button1);
		mTextbox = (EditText)findViewById(R.id.editText1);
		
		pref = getSharedPreferences("ERA", MODE_PRIVATE);
		editor = pref.edit();
		
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// save current calibration value
				String value = mTextbox.getText().toString();
				editor.putFloat("era_calib", Float.parseFloat(value)).commit();
				
				/**
				 * 
				 * put binary data creation code here 
				 * 
				 * (use EraCore object created at the class initialization)
				 * 
				 * 
				 */
				
				Intent intent = new Intent(TempSetting.this, MainActivity.class);
				// we don't need to revisit	
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
				
			}
		});
	}
	
	

}
