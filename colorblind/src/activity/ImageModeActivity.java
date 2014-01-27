package activity;

import utililty.ImageProcessHelper;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.naubull2.colorblind.R;

public class ImageModeActivity extends Activity {
	
	private static final String TAG = "REFINE_FROM_GALLERY";
	
	private static final int SET_IMAGE_FROM_GALLERY = 1;
	private static final int CLEAR_UPDATE_SCREEN = 2;
	
	private ImageProcessHelper mImageProcHelper;
	private ImageView mImageView;
	private Uri selectedImage;	
	
	
	
	
	private Handler mMainHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case SET_IMAGE_FROM_GALLERY:
				Bitmap initialBitmap = decodeUri(selectedImage);
				Bitmap preparedBitmap = ImageProcessHelper.JPEGtoRGB888(initialBitmap);
				
				break;
			case CLEAR_UPDATE_SCREEN:
				
			}
		}
	};
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.i("GALLERY", "recieved image data from gallery");
		
		// 전체 화면, 타이틀 액션바 제거
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		selectedImage = getIntent().getData();
		
		/*
		 * extract bitmap data from URI
		 * 
		 * Bitmap initialBitmap = decodeUri(selectedImage);
		 * Bitmap scaledImage = Bitmap.createScaledBitmap(
		 * 								initialBitmap, WIDTH, HEIGHT, true);
		 * scaledImage = mImageProcHelper.JPEGtoRGB888(scaledImage);
		 * initialBitmap.recycle();
		 */

		setContentView(R.layout.activity_image_mode);
		mImageView = (ImageView)findViewById(R.id.image_view);
		
		mMainHandler.sendEmptyMessage(SET_IMAGE_FROM_GALLERY);
		finish();
	}

}
