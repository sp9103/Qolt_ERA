package utility;

import java.io.FileOutputStream;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

public class CameraPreviewSurface extends JavaCameraView {

    private static final String TAG = "PREVIEW_SURFACE";

    public CameraPreviewSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Camera getCamera(){
    	return mCamera;
    }
    
	@Override
	protected boolean initializeCamera(int width, int height) {
		boolean noError = super.initializeCamera(width, height);
		
		Camera.Parameters params;
		params = mCamera.getParameters();
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(params);
        
        return noError;
	}

	public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public void reduceResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height/2;
        mMaxWidth = resolution.width/2;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Tacking picture");
        PictureCallback callback = new PictureCallback() {

            private String mPictureFileName = fileName;

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i(TAG, "Saving a bitmap to file");
                Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                try {
                    FileOutputStream out = new FileOutputStream(mPictureFileName);
                    picture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    picture.recycle();
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mCamera.takePicture(null, null, callback);
    }
}
