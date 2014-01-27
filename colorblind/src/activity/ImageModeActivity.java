package activity;

import java.io.FileNotFoundException;

import utililty.ImageProcessHelper;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class ImageModeActivity extends Activity {
	
	private static final String TAG = "REFINE_FROM_GALLERY";
	
	private static final int SET_IMAGE_FROM_GALLERY = 1;
	private static final int CLEAR_UPDATE_SCREEN = 2;
	
	private ImageProcessHelper mImageProcHelper;
	private ImageView mImageView;
	private Uri selectedImage;	
	private Context mContext;
	
	
	
	
	private Handler mMainHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			
			case SET_IMAGE_FROM_GALLERY:
				Bitmap initialBitmap;
				try {
					initialBitmap = decodeUri(selectedImage);
					Bitmap preparedBitmap = ImageProcessHelper.JPEGtoRGB888(initialBitmap);
					
					mImageView.setImageBitmap(preparedBitmap);
					
				} catch (FileNotFoundException e) {
					Toast.makeText(mContext, "Image not found!!", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
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
		
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		// Get image size
		BitmapFactory.decodeStream(getContentResolver().openInputStream(inImage),null,imgOption);
		
		int scale = 1;
		if (imgOption.outWidth > dm.widthPixels)
			scale = imgOption.outWidth / dm.widthPixels;
		
		BitmapFactory.Options outOption = new BitmapFactory.Options();
		outOption.inSampleSize = scale;
		
		return BitmapFactory.decodeStream(getContentResolver().openInputStream(inImage), null, outOption);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.i("GALLERY", "recieved image data from gallery");
		
		mContext = this;
		
		// 전체 화면, 타이틀 액션바 제거
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		selectedImage = getIntent().getData();

		setContentView(R.layout.activity_image_mode);
		mImageView = (ImageView)findViewById(R.id.image_view);
		
		mMainHandler.sendEmptyMessage(SET_IMAGE_FROM_GALLERY);
		finish();
	}

}
