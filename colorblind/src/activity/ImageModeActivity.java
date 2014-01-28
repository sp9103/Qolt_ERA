package activity;

import java.io.FileNotFoundException;

import libera.EraCore;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import utililty.ImageProcessHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class ImageModeActivity extends Activity {
	
	private static final String TAG = "REFINE_FROM_GALLERY";
	
	private static final int SET_IMAGE_FROM_GALLERY = 1;
	private static final int CLEAR_UPDATE_SCREEN = 2;
	
	private ImageProcessHelper mImageProcHelper = new ImageProcessHelper();
	private EraCore era = new EraCore();
	private ImageView mImageView;
	private Uri selectedImage;	
	private Context mContext;
	
	
	
	
	
	private Handler mMainHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			
			case SET_IMAGE_FROM_GALLERY:
				Log.i(TAG, "reading image");
				ProgressDialog dialog = new ProgressDialog(mContext);
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dialog.setIndeterminate(true);
				dialog.setCancelable(false);
				dialog.setMessage("Processing");
				dialog.show();
				
				Bitmap initialBitmap;
				try {
					initialBitmap = decodeUri(selectedImage);
					Bitmap preparedBitmap = mImageProcHelper.JPEGtoRGB888(initialBitmap);
					initialBitmap.recycle();
					
					Mat srcImage = mImageProcHelper.BitmapToMat(mImageProcHelper.JPEGtoRGB888(preparedBitmap));
					Mat destImage = new Mat();
					
					era.RefineImage(srcImage.nativeObj, destImage.nativeObj, (float)0.4);
					srcImage.release();
					
					preparedBitmap = mImageProcHelper.MatToBitmap(destImage);
					
					mImageView.setImageBitmap(preparedBitmap);
					
				} catch (FileNotFoundException e) {
					Toast.makeText(mContext, "Image not found!!", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				
				dialog.dismiss();
				break;
				
			case CLEAR_UPDATE_SCREEN:
				
			}
		}
	};
	
	/*
	 * Resize image bitmap to fit screen, preventing OOM for loading large images
	 * Throws File not found exception if URI is null
	 */
	private Bitmap decodeUri(Uri inImage) throws FileNotFoundException {	
		BitmapFactory.Options imgOption = new BitmapFactory.Options();
		imgOption.inJustDecodeBounds = true;	// prevents wrong URI bugs
		
		Log.i(TAG, "getting screen dimension");
		
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		Log.i(TAG, "screen width"+width);

		// Get image size
		BitmapFactory.decodeStream(getContentResolver().openInputStream(inImage),null,imgOption);
		Log.i(TAG, "image"+imgOption.outWidth+"*"+imgOption.outHeight);
		
		int scale = 1;
		if (imgOption.outWidth > width)
			scale = imgOption.outWidth / width;
		Log.i(TAG, "scale factor "+scale);
		
		BitmapFactory.Options outOption = new BitmapFactory.Options();
		outOption.inSampleSize = scale;
		
		return BitmapFactory.decodeStream(getContentResolver().openInputStream(inImage), null, outOption);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.i(TAG, "recieved image data from gallery");
		
		mContext = this;
		
		// 전체 화면, 타이틀 액션바 제거
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_image_mode);
		mImageView = (ImageView)findViewById(R.id.image_view);
		
		Log.i(TAG, "retrieving image data uri");
		selectedImage = getIntent().getData();
		
		if(!OpenCVLoader.initDebug()){
	    	Log.d("CVerror","OpenCV library Init failure");
		}else{
			mMainHandler.sendEmptyMessage(SET_IMAGE_FROM_GALLERY);
		}
	}

}
