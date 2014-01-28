package activity;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import utililty.CameraPreviewSurface;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.naubull2.colorblind.R;

public class CameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
	private static final String TAG = "CAMERA_MODE";

	private CameraPreviewSurface mOpenCvCameraView;
	
	private Context mContext;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		mContext = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (!OpenCVLoader.initDebug()) {
			Log.i("OPENCV", "open CV static load error");
		}
		setContentView(R.layout.activity_camera);

		mOpenCvCameraView = (CameraPreviewSurface) findViewById(R.id.cv_surface_view);

		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

		mOpenCvCameraView.setCvCameraViewListener(this);
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
		return inputFrame.rgba();
	}
	

	@SuppressLint("SimpleDateFormat")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i(TAG, "onTouch event");

		return false;
	}
}
