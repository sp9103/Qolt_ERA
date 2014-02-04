package activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import libera.EraCore;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import utility.BitmapFrameSingleton;
import utility.ImageProcessHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class CameraResultActivity extends Activity{
private static final String TAG = "REFINE_FROM_GALLERY";
	
	private static final int SET_IMAGE_FROM_CAMERA = 1;
	private static final int CLEAR_UPDATE_SCREEN = 2;
	
	private ImageProcessHelper mImageProcHelper = new ImageProcessHelper();
	private EraCore era = new EraCore();
	private ImageView mImageView;
	private Context mContext;
	
	private SharedPreferences pref;
	
	private Bitmap initialImage;
	private Bitmap resultImage;
	
	static BitmapFrameSingleton singleton = BitmapFrameSingleton.getInstance();
	
	private Handler mMainHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			
			case SET_IMAGE_FROM_CAMERA:
				//mImageView.setImageBitmap(initialImage);
				new ImageRefineTask().execute();
				break;
				
			case CLEAR_UPDATE_SCREEN:
				
			}
		}
	};
	
	
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

			//resultImage = mImageProcHelper.JPEGtoRGB888(initialImage);

			Mat srcImage = mImageProcHelper.BitmapToMat(initialImage);
			//Mat destImage = new Mat();

			pref = PreferenceManager.getDefaultSharedPreferences(mContext);
			era.RefineImage(srcImage.nativeObj, srcImage.nativeObj, pref.getFloat("era_calib", (float)0.4));
			//srcImage.release();

			resultImage = mImageProcHelper.MatToBitmap(srcImage);
			initialImage = rotate(initialImage, 90);
			resultImage = rotate(resultImage,90);

			return true;
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
				File file = new File(dir, "ERA_refine_" + pref.getInt("refine_index", 0) + ".jpg");
				FileOutputStream fOut;
				
				fOut = new FileOutputStream(file);
				resultImage.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
				fOut.flush();
				fOut.close();
				SharedPreferences.Editor edit = pref.edit();
				edit.putInt("refine_index", pref.getInt("refine_index",0)+1).commit();
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
		
		getActionBar().setTitle(R.string.title_camera);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_image_mode);
		mImageView = (ImageView)findViewById(R.id.image_view);
		
		Log.i(TAG, "retrieving image");
		initialImage = singleton.getImage();
		
		Log.i(TAG, "imagesize: "+initialImage.getByteCount());
	
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
				mMainHandler.sendEmptyMessage(SET_IMAGE_FROM_CAMERA);
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
	public static Bitmap rotate(Bitmap b, int degrees) {
        if ( degrees != 0 && b != null ) {
            Matrix m = new Matrix();
            m.setRotate( degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2 );
            try {
                Bitmap b2 = Bitmap.createBitmap( b, 0, 0, b.getWidth(), b.getHeight(), m, true );
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return b;
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