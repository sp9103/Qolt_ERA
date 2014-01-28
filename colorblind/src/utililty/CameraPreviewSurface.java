package utililty;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class CameraPreviewSurface extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener{
	private static final String TAG = "SURFACEVIEW";
	
	private Handler mainHandler;
	private SurfaceHolder mHolder;
	
	
	public CameraPreviewSurface(Context context, Handler handler) {
		super(context);
		mainHandler = handler;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setKeepScreenOn(true);
	}


	/*
	 * Preview callback (handling frames)
	 */
	private Camera.PreviewCallback mPreviewCallback = new PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] _data, Camera _camera) {
			// try rendering here?
		}
	};
	
	
	/*
	 * get optimal preview size
	 */
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            Log.d(TAG, "Checking size " + size.width + "w " + size.height
                    + "h");
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the
        // requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
	
	/*
	 * set display orientation && orientation listener
	 */
	public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
	
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		// setCameraDisplayOrientation(CameraPreviewSurface.this, mCameraId, mCamera);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}

	/*
	 * Preview touch will trigger auto focus
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
