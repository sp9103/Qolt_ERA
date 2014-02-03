package activity;

import com.naubull2.colorblind.R;

import customui.SettingFragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.title_setting);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	 
	    switch (item.getItemId()) {
	        case android.R.id.home: {
	            finish();
	            break;
	        }
	    }
	    return super.onOptionsItemSelected(item);
	}

}
