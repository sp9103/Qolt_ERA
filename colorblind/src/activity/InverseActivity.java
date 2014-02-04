package activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import libera.EraCore;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import utility.ImageProcessHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class InverseActivity extends Activity {
	
	private static final String TAG = "REFINE_FROM_GALLERY";
	
	private static final int SET_IMAGE_FROM_GALLERY = 1;
	private static final int CLEAR_UPDATE_SCREEN = 2;
	
	private ImageProcessHelper mImageProcHelper = new ImageProcessHelper();
	private EraCore era = new EraCore();
	private ImageView mImageView;
	private Uri selectedImage;	
	private Context mContext;
	
	private SharedPreferences pref;
	
	private Bitmap initialImage;
	private Bitmap resultImage;
	
	
	private Handler mMainHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			
			case SET_IMAGE_FROM_GALLERY:
				
				new ImageInverseTask().execute();
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
	
	class ImageInverseTask extends AsyncTask<Void, Void, Boolean> {
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
			
			
			
			try {
				initialImage = decodeUri(selectedImage);
				resultImage = mImageProcHelper.JPEGtoRGB888(initialImage);
				
				Mat srcImage = mImageProcHelper.BitmapToMat(resultImage);
				//Mat destImage = new Mat();
				
				pref = PreferenceManager.getDefaultSharedPreferences(mContext);
				era.InverseImage(srcImage.nativeObj, srcImage.nativeObj, pref.getFloat("inverse_calib", (float)0.4));
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
			}else{
				Toast.makeText(mContext, "Press to compare", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class SaveResultBitmapTask extends AsyncTask<Void, Void, Boolean>{
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// Opens dialog on loading data file to external storage
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Saving");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
						"/ERA";
				File dir = new File(file_path);
				if(!dir.exists())
					dir.mkdirs();
				File file = new File(dir, "ERA_inverse_" + pref.getInt("inverse_index", 0) + ".jpg");
				FileOutputStream fOut;
				
				fOut = new FileOutputStream(file);
				resultImage.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
				fOut.flush();
				fOut.close();
				SharedPreferences.Editor edit = pref.edit();
				edit.putInt("inverse_index", pref.getInt("inverse_index",0)+1).commit();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean isDone) {
			pDialog.dismiss();
			
			if(!isDone){
				Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
				finish();
			}else{
				Toast.makeText(mContext, "Saved!", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	/* Save bitmaps to prevent releading and reprocessing */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("bitmap", resultImage);
		outState.putParcelable("origin", initialImage);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.i(TAG, "recieved image data from gallery");
		
		mContext = this;
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		getActionBar().setTitle(R.string.title_trial);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_inverse_mode);
		mImageView = (ImageView)findViewById(R.id.image_view);
		
		Log.i(TAG, "retrieving image data uri");
		selectedImage = getIntent().getData();
	
		if(savedInstanceState != null){
			Log.i(TAG, "loaded saved Image");
			resultImage = savedInstanceState.getParcelable("bitmap");
			initialImage = savedInstanceState.getParcelable("origin");
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
		mImageView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				
				case MotionEvent.ACTION_DOWN:
					mImageView.setImageBitmap(initialImage);
					break;
					
				case MotionEvent.ACTION_UP:
					mImageView.setImageBitmap(resultImage);
					break;
				}
				return true;
			}
		});
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		initialImage.recycle();
		resultImage.recycle();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	            break;
	        case R.id.action_save:
	        	new SaveResultBitmapTask().execute();
	            	
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
	
}