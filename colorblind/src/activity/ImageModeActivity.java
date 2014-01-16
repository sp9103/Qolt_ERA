package activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

public class ImageModeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Uri selectedImage = getIntent().getData();
	}

}
