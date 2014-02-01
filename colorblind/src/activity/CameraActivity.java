package activity;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import utility.CameraPreviewSurface;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.naubull2.colorblind.R;

public class CameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener{
	private static final String TAG = "CAMERA_MODE";

	private CameraPreviewSurface mOpenCvCameraView;
	private Button mButtonShutter;
	private ImageView mFocusImage;
	
	private Context mContext;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		mContext = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide softkey for immersive mode on kitkat and above
		if(android.os.Build.VERSION.SDK_INT >= 19) {
			hideSystemUI();
		}
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (!OpenCVLoader.initDebug()) {
			Log.i("OPENCV", "open CV static load error");
		}
		setContentView(R.layout.activity_camera);

		//mFocusImage = (ImageView)findViewById(R.id.focus_ui);
		//mFocusImage.setBackgroundResource(R.drawable.focus_default);
		
		mOpenCvCameraView = (CameraPreviewSurface) findViewById(R.id.cv_surface_view);
		mButtonShutter = (Button) findViewById(R.id.button_shutter);
		mButtonShutter.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch(action){
				case MotionEvent.ACTION_DOWN:
					mOpenCvCameraView.getCamera().autoFocus(mFocus);
					//mFocusImage.setBackgroundResource(R.drawable.focus_default);
					break;
				case MotionEvent.ACTION_OUTSIDE:
					mOpenCvCameraView.getCamera().cancelAutoFocus();
					//mFocusImage.setBackgroundResource(R.drawable.focus_false);
					break;
				case MotionEvent.ACTION_UP:
					// Take photo
				}
				return false;
			}
		});

		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	private void hideSystemUI() {
	    // Set the IMMERSIVE flag
		getWindow().getDecorView().setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (!OpenCVLoader.initDebug()) {
			Log.i("OPENCV", "open CV static load error");
		} else {
			mOpenCvCameraView.enableView();
			mOpenCvCameraView.setOnTouchListener(CameraActivity.this);
			
			// For dev only code
			mOpenCvCameraView.enableFpsMeter();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
	}

	public void onCameraViewStopped() {
	}

	/*
	 * Here we will add image filter on live preview frames
	 */
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Log.d(TAG, "rows "+inputFrame.rgba().rows() + ",col "+inputFrame.rgba().cols());
		return inputFrame.rgba();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i(TAG, "onTouch event");
		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			mOpenCvCameraView.getCamera().autoFocus(mFocus);
			//mFocusImage.setBackgroundResource(R.drawable.focus_default);
			break;
		case MotionEvent.ACTION_OUTSIDE:
			mOpenCvCameraView.getCamera().cancelAutoFocus();
			//mFocusImage.setBackgroundResource(R.drawable.focus_false);
			break;
		case MotionEvent.ACTION_UP:
			mOpenCvCameraView.getCamera().cancelAutoFocus();
		}
		return false;
	}

	
	private AutoFocusCallback mFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success){
				//mFocusImage.setBackgroundResource(R.drawable.focus_true);
			}
		}
	};
}
