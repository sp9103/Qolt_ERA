package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class CameraActivity extends Activity{
	
	/*
	 * 
	 * bunch of member objects
	 * 
	 */
	static final String TAG = "CAMERA"; // Logcat TAG

	private Context mContext = this;
	private FrameLayout preview;
	private Camera mCamera;
	private CameraPreview mPreview;
	private RadioButton mCheck;
	private Button mTakePhoto, mAlbumButton, mSettingButton;

	public Handler startProcessing;

	private boolean isFocused = false;
	private boolean errorFound = false;
	private boolean isFocusing = false;
	private boolean isTraining = true;

	private int cameraId = -1;

	

	/*
	 * Main handler for posting messages to main UI thread
	 */
	
	/*
	 * Initializer method for onCreate or onResume
	 */
	public void initialization(){
		
		mCheck.setChecked(false);
		isFocused = false;


		// 카메라 인스턴스 생성
		if (checkCameraHardware(mContext)) {
			// 프리뷰창을 생성하고 액티비티의 레이아웃으로 지정합니다
			mCamera = getCameraInstance(0);
			mCamera.setDisplayOrientation(0);	
			preview = (FrameLayout) findViewById(R.id.camera_preview);
			mPreview = new CameraPreview(this, startProcessing);
			mPreview.setCamera(mCamera);

			preview.addView(mPreview);

			// 촬영용 버튼 리스너 생성
			mTakePhoto = (Button) findViewById();
			mTakePhoto.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();

					switch (action) {
					case MotionEvent.ACTION_DOWN:
						// 포커싱
						if (isTraining
								|| dataManager.getData("imgIndex",
										Integer.class) != 0) {
							if (!isFocusing && !isFocused) {
								isFocusing = true;
								mCamera.autoFocus(mFocus);
							}
							break;
						} else
							Toast.makeText(mContext,
									"Training set not prepared",
									Toast.LENGTH_SHORT).show();
					case MotionEvent.ACTION_UP:
						break;
					}
					return false;
				}
			});

		} else if (CameraInfo.CAMERA_FACING_FRONT > -1) {
			// 넥서스7 처럼 후면 카메라가 없는 기기 처리
			try {
				cameraId = CameraInfo.CAMERA_FACING_FRONT;
				mCamera = Camera.open(cameraId);
			} catch (Exception e) {
				errorFound = true;
			}
			if (errorFound = true) {
				try {
					mCamera = Camera.open(0);
					cameraId = 0;
				} catch (Exception e) {
					cameraId = -1;
				}
			}
		}
	}
	
	/*
	 * picture callback
	 */
	
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) { 
			Log.i(TAG, data.length + " size");

		}
	};
		 

	
	
	

	/*
	 * focus callback
	 */
	private AutoFocusCallback mFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
		
		}
	};

	/*
	 * get camera instance
	 */
	
	/* check camera availability */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// 후면 카메라가 존재하는 경우
			return true;
		} else {
			// 전면 카메라가 없는 경우
			Toast.makeText(mContext, "No camera found!", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
	}

	// safely get camera instance
	public Camera getCameraInstance(int id) {
		Camera c = null;
		try {
			releaseCameraAndPreview();
			c = Camera.open(id);
		} catch (Exception e) {
			// not available
			e.printStackTrace();
		}
		return c;
	}
	/*
	 * release camera instance
	 */



	private void releaseCameraAndPreview() {
		if (mPreview != null) {
			preview.removeAllViews();
		}
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mPreview.getHolder().removeCallback(mPreview);
			mCamera.release();
			mCamera = null;
		}
	}

	/*
	 * onCreate()
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 전체 화면, 타이틀 액션바 제거
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView();
		mContext = this;

		SharedPreferences pref = getSharedPreferences("EDGE", MODE_PRIVATE);

		mTakePhoto = (Button) findViewById(R.id.touchListener);
		
		startProcessing = new Handler() {
			public void handleMessage(Message msg) {
				Log.i(TAG, "image capture finished");
				Intent i = new Intent(mContext, ProcessActivity.class);
				mContext.startActivity(i);
			}
		};
	}
	
	/*
	 * onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "on Resume");

		preview = (FrameLayout) findViewById(R.id.camera_preview);
		mPreview = new CameraPreview(this, startProcessing);
		mPreview.setCamera(mCamera);

		preview.addView(mPreview);
		
	}

	/*
	 * onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "on Pause");

		releaseCameraAndPreview();
	}

	/**
	// 메모리 부족 문제 발생을 대비한 다운샘플링 메소드
	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		final int REQUIRED_WIDTH = IMG_WIDTH;
		// 이미지 사이즈 디코딩
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o);

		// 이미지가 클경우에는, 스케일 사이즈를 대략 적으로만 계산
		int scale = 1;
		if (o.outWidth > REQUIRED_WIDTH) {
			scale = o.outWidth / REQUIRED_WIDTH;
		}
		// 샘플 사이즈로 디코딩
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;

		return BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o2);
	}
	 **/
}
