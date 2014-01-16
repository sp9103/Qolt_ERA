package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.naubull2.colorblind.R;

public class MainActivity extends Activity {
	private final int 		SELECT_IMAGE = 41;
	private static Context 	mContext;			
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = getBaseContext();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		
		/* launch activity accordingly */
		Button buttonTest = (Button)findViewById(R.id.main_button_test);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, );
//				startActivity(intent);
			}
		});
		
		Button buttonDemo = (Button)findViewById(R.id.main_button_demo);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, );
//				startActivity(intent);
			}
		});
		
		Button buttonImage = (Button)findViewById(R.id.main_button_image);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, SELECT_IMAGE);
			}
		});
		
		Button buttonCamera = (Button)findViewById(R.id.main_button_camera);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, );
//				startActivity(intent);
			}
		});
		
		Button buttonMovie = (Button)findViewById(R.id.main_button_movie);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, );
//				startActivity(intent);
			}
		});
		
		Button buttonSetting = (Button)findViewById(R.id.main_button_setting);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, );
//				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		
		switch (requestCode) {
		case SELECT_IMAGE:
			if (resultCode == RESULT_OK) { // proper image should be selected
				Uri selectedImage = imageReturnedIntent.getData();
				
			    // send uri of the selected image to the processing activity
				Intent intent = new Intent(mContext, ImageModeActivity.class);
				intent.setData(selectedImage);
				startActivity(intent);
			}
		}
	}
}
