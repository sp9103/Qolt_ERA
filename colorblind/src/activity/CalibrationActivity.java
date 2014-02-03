package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.naubull2.colorblind.R;

public class CalibrationActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.title_calibration);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_calibration);
	}

	/*
	 * Show pop up to double check exit and save any values currently set
	 */
	public void exitDialog(){
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.dlg_title_calibration)
        .setMessage(R.string.dlg_content_calibration)
        .setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	/*
            	 * Save current value
            	 */
            	
                // Stop Activity
            	CalibrationActivity.this.finish();    
            }
        })
        .setNegativeButton(R.string.dlg_no, null)
        .show();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	 
	    switch (item.getItemId()) {
	        case android.R.id.home: {
	        	exitDialog();
	            break;
	        }
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Confirm and save whatever is done before exit
	 */
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // Ask if it should really quit
        	exitDialog();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
