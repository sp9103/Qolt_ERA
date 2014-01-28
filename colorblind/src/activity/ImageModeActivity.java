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
import android.os.AsyncTask;
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
	
	private Bitmap resultImage;
	
	
	private Handler mMainHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			
			case SET_IMAGE_FROM_GALLERY:
				
				new ImageRefineTask().execute();
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
	
	class ImageRefineTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// Opens dialog on loading data file to external storage
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Processing");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... arg) {
			Log.i(TAG, "reading image");
			
			
			Bitmap initialBitmap;
			try {
				initialBitmap = decodeUri(selectedImage);
				resultImage = mImageProcHelper.JPEGtoRGB888(initialBitmap);
				initialBitmap.recycle();
				
				Mat srcImage = mImageProcHelper.BitmapToMat(mImageProcHelper.JPEGtoRGB888(resultImage));
				//Mat destImage = new Mat();
				
				era.RefineImage(srcImage.nativeObj, srcImage.nativeObj, (float)0.4);
				//srcImage.release();
				
				resultImage = mImageProcHelper.MatToBitmap(srcImage);
				
				return true;
			} catch (FileNotFoundException e) {
				Toast.makeText(mContext, "Image not found!!", Toast.LENGTH_LONG).show();
				e.printStackTrace();
				return false;
			}
			
		}
		@Override
		protected void onPostExecute(Boolean isDone) {
			pDialog.dismiss();
			mImageView.setImageBitmap(resultImage);
			if(!isDone){
				Toast.makeText(mContext, "Process error!", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
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
	
		if(savedInstanceState != null){
			Log.i(TAG, "loaded saved Image");
			resultImage = savedInstanceState.getParcelable("bitmap");
			mImageView.setImageBitmap(resultImage);
		}else{
			resultImage = null;
		}
		
		if(resultImage == null){
			if(!OpenCVLoader.initDebug()){
				Log.d("CVerror","OpenCV library Init failure");
			}else{
				Log.i(TAG, "start processing");
				mMainHandler.sendEmptyMessage(SET_IMAGE_FROM_GALLERY);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("bitmap", resultImage);
	}
	
}