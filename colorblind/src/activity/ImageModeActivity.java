package activity;

import utililty.ImageProcessHelper;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.naubull2.colorblind.R;

public class ImageModeActivity extends Activity {
	private ImageProcessHelper mImageProcHelper;
	private ImageView mImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.i("GALLERY", "recieved image data from gallery");
		Uri selectedImage = getIntent().getData();
		
		mImageView = (ImageView)findViewById(R.id.imageView);
		setContentView(R.layout.fragment_full_image);
		
		/*
		 * extract bitmap data from URI
		 * 
		 * Bitmap initialBitmap = decodeUri(selectedImage);
		 * Bitmap scaledImage = Bitmap.createScaledBitmap(
		 * 								initialBitmap, WIDTH, HEIGHT, true);
		 * scaledImage = mImageProcHelper.JPEGtoRGB888(scaledImage);
		 * initialBitmap.recycle();
		 */
	}

}
