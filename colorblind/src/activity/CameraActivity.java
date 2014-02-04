package activity;

import libera.EraCore;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import utility.CameraPreviewSurface;
import utility.ImageProcessHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class CameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener{
	private static final String TAG = "CAMERA_MODE";
	private static final int 	MODE_NORMAL_CAM = 1;
	private static final int	MODE_REFINE_CAM = 2;
	private static final int 	MODE_EXP_CAM	= 3;

	private CameraPreviewSurface mOpenCvCameraView;
	private Button mButtonShutter;
	private Button mButtonMode;
	private ImageView mFocusImage;
	private int mCameraMode;
	
	// call native methods through era object here
	private ImageProcessHelper mImageProcHelper/* = new ImageProcessHelper()*/;
	private EraCore era/* = new EraCore()*/;

	
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
		
		mImageProcHelper = new ImageProcessHelper();
		era = new EraCore();
		
		// Default mode is normal cam
		mCameraMode = MODE_NORMAL_CAM;
		
		mOpenCvCameraView = (CameraPreviewSurface) findViewById(R.id.cv_surface_view);
		mButtonShutter = (Button) findViewById(R.id.button_shutter);
		mButtonShutter.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch(action){
				case MotionEvent.ACTION_DOWN:
					mFocusImage.setBackgroundResource(R.drawable.focus_default);
					mOpenCvCameraView.getCamera().autoFocus(mFocus);
					break;
				case MotionEvent.ACTION_OUTSIDE:
					mOpenCvCameraView.getCamera().cancelAutoFocus();
					mFocusImage.setBackgroundResource(R.drawable.focus_false);
					break;
				case MotionEvent.ACTION_UP:
					// Take photo
				}
				return false;
			}
		});
		mButtonMode = (Button)findViewById(R.id.button_mode);
		mButtonMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String noti = new String();
				mCameraMode = mCameraMode%3 + 1;
				switch(mCameraMode){
				case MODE_NORMAL_CAM:
					noti = getString(R.string.cam_mode_normal);
					break;
				case MODE_REFINE_CAM:
					noti = getString(R.string.cam_mode_era);
					break;
				case MODE_EXP_CAM:
					noti = getString(R.string.cam_mode_trial);
				}
				Toast.makeText(mContext, noti, Toast.LENGTH_SHORT).show();
			}
		});
		mFocusImage = (ImageView)findViewById(R.id.focus_ui);
		mFocusImage.setBackgroundResource(R.drawable.focus_default);

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
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
		/**
		 * execute data loading here
		 */
		Log.i(TAG, "onCameraViewStarted start");
		
		// era.
		// era - open ImageCorrection Mode 
		Log.i(TAG, "Open DataSet start!");
		era.OpenDataFile("/sdcard/Pictures/ERA/ImgCorrectionData.bin", 0);
		
		Size resolution = mOpenCvCameraView.getResolution();
		mOpenCvCameraView.reduceResolution(resolution);
		//mOpenCvCameraView.SetCaptureFormat(format)
		
		
		//new InitiateDataTask().execute();
	}

	public void onCameraViewStopped() {
		// era Data reset, memory return
		era.DeleteDataBuffer();
	}

	/*
	 * Here we will add image filter on live preview frames
	 */
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Log.d(TAG, "rows "+inputFrame.rgba().rows() + ",col "+inputFrame.rgba().cols());
		
		// input frame Depth CV_8U, 4 channel.
		//int depth = inputFrame.rgba().depth();
		//int channel = inputFrame.rgba().channels();
		//inputFrame.rgba().
		
		Mat preview = new Mat(inputFrame.rgba().rows(), inputFrame.rgba().cols(), CvType.CV_8UC4); 
		
		/**
		 * Do the image filter here ( no async task needed : probably...)
		 */
		era.MakeImgtoData(inputFrame.rgba().nativeObj, preview.nativeObj);
		
		return preview;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i(TAG, "onTouch event");
		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			mFocusImage.setBackgroundResource(R.drawable.focus_default);
			mOpenCvCameraView.getCamera().autoFocus(mFocus);
			break;
		case MotionEvent.ACTION_UP:
			if(android.os.Build.VERSION.SDK_INT >= 19)
				hideSystemUI();
		}
		return false;
	}

	
	private AutoFocusCallback mFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success){
				mFocusImage.setBackgroundResource(R.drawable.focus_true);
			}else{
				mFocusImage.setBackgroundResource(R.drawable.focus_false);
			}
		}
	};
	
	class InitiateDataTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Initiating...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... arg) {
			Log.i(TAG, "reading image");

			/**
			 * initiate native data here 
			 * 
			 * (use EraCore object created at the class initialization)
			 * 
			 */
			// era.
			// era - open ImageCorrection Mode 
			//era.OpenDataFile("/sdcard/Pictures/ERA/ImgCorrectionData.bin", 0);
			
			return true;
		}
		@Override
		protected void onPostExecute(Boolean isDone) {
			pDialog.dismiss();
		}
	}	
}
