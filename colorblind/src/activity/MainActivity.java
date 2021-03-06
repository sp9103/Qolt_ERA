package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class MainActivity extends Activity {
	private final int 		REFINE_IMAGE = 41;
	private final int		INVERSE_IMAGE = 43;
	private static Context 	mContext;			
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mContext = getBaseContext();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		/* launch activity accordingly */
		Button buttonTest = (Button)findViewById(R.id.main_button_test);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CalibrationActivity.class);
				startActivity(intent);
			}
		});
		
		Button buttonDemo = (Button)findViewById(R.id.main_button_demo);
		buttonDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("GALLERY", "launch gallery for image selection");
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, INVERSE_IMAGE);
			}
		});
		
		Button buttonImage = (Button)findViewById(R.id.main_button_image);
		buttonImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("GALLERY", "launch gallery for image selection");
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, REFINE_IMAGE);
			}
		});
		
		Button buttonCamera = (Button)findViewById(R.id.main_button_camera);
		buttonCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CameraActivity.class);
				startActivity(intent);
			}
		});
		
		Button buttonMovie = (Button)findViewById(R.id.main_button_movie);
		buttonMovie.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "In development", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(mContext, );
//				startActivity(intent);
			}
		});
		
		Button buttonSetting = (Button)findViewById(R.id.main_button_setting);
		buttonSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SettingActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		Log.i("IMAGESELECT", "returned from gallery");
		switch (requestCode) {
		case REFINE_IMAGE:
			if (resultCode == RESULT_OK) { // proper image should be selected
				Uri selectedImage = imageReturnedIntent.getData();
				
			    // send uri of the selected image to the processing activity
				Intent intent = new Intent(mContext, ImageModeActivity.class);
				intent.setData(selectedImage);
				startActivity(intent);
				break;
			}
		case INVERSE_IMAGE:
			if (resultCode == RESULT_OK){
				Uri selectedImage = imageReturnedIntent.getData();
				
				// send uri of the selected image to the processing activity
				Intent intent = new Intent(mContext, InverseActivity.class);
				intent.setData(selectedImage);
				startActivity(intent);
			}
		}
	
	}
}
