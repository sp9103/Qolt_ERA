package utility;

import org.opencv.android.JavaCameraView;

import activity.CameraResultActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

public class CameraPreviewSurface extends JavaCameraView {
	static BitmapFrameSingleton singleton = BitmapFrameSingleton.getInstance();
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

    public void takePicture(final Context parent) {
        Log.i(TAG, "Tacking picture");
        PictureCallback callback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i(TAG, "Saving a bitmap to singleton");
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                Log.i(TAG, "Bitmap size:"+picture.getWidth()+"*"+picture.getHeight());
                singleton.setImage(picture);
                Intent intent = new Intent(parent, CameraResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                parent.startActivity(intent);
                ((Activity)parent).finish();
            }
        };

        mCamera.takePicture(null, null, callback);
    }
}
