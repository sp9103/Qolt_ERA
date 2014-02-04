package customui;

import java.io.File;

import libera.EraCore;
import utility.ImageProcessHelper;
import activity.MainActivity;
import activity.SplashActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.naubull2.colorblind.R;

public class SettingFragment extends PreferenceFragment implements OnPreferenceClickListener{
	private Preference mNotice, mVersion, mAbout, mHelp, mEraValue, mReset;
	private ListPreference mExpValue;
	
	private SharedPreferences mPref;
	private SharedPreferences.Editor editor;
	
	private ImageProcessHelper mImageProcHelper = new ImageProcessHelper();
	// call native methods through era object here
	private EraCore era = new EraCore();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_setting);
        
        mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = mPref.edit();
        
        mNotice = (Preference)findPreference("key_notice");
        mVersion = (Preference)findPreference("key_version");
        mAbout = (Preference)findPreference("key_about");
        mHelp = (Preference)findPreference("key_help");
        
        mEraValue = (Preference)findPreference("key_era_value");
        //mInterval = (Preference)findPreference("key_interval");
        mReset = (Preference)findPreference("key_reset");
        mExpValue = (ListPreference)findPreference("key_exp_value");
        mExpValue.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        	// converts selected value to float and save to default sharedpreference
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				editor.putFloat("inverse_calib", Float.parseFloat(newValue.toString())).commit();
				new NativeTask().execute(1);
				return true;
			}
		});

        mNotice.setOnPreferenceClickListener(this);
        mVersion.setOnPreferenceClickListener(this);
        mAbout.setOnPreferenceClickListener(this);
        mHelp.setOnPreferenceClickListener(this);
        
        mEraValue.setOnPreferenceClickListener(this);
        //mInterval.setOnPreferenceClickListener(this);
        mReset.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
    	LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout;

    	if(pref == mNotice)
    	{
    		Toast.makeText(getActivity(), getResources().getString(R.string.toast_notice), Toast.LENGTH_SHORT).show();
    		// notice
    	}
    	else if(pref == mVersion)
    	{
    		Toast.makeText(getActivity(), getResources().getString(R.string.toast_version), Toast.LENGTH_SHORT).show();
    		// version
    	}
    	else if(pref == mAbout)
    	{
    		Toast.makeText(getActivity(), getResources().getString(R.string.toast_not_ready), Toast.LENGTH_SHORT).show();
    		// developer information
    	}
    	else if(pref == mHelp)
    	{
    		Toast.makeText(getActivity(), getResources().getString(R.string.toast_not_ready), Toast.LENGTH_SHORT).show();
    		// help activity
    	}
    	else if(pref == mEraValue)
    	{
    		final Dialog dlgRefine = new Dialog(getActivity());
			layout = inflater.inflate(R.layout.dialog_seekbar, null);
			dlgRefine.setContentView(layout);
			dlgRefine.setTitle(getResources().getString(R.string.dlg_title_refine));
			
			// layout
			final TextView dlgText = (TextView) layout.findViewById(R.id.value_dialog);
			final SeekBar dlgSeekBar = (SeekBar) layout.findViewById(R.id.seekbar_dialog);
			Button dlgButton = (Button) layout.findViewById(R.id.btn_dialog);
			
			dlgText.setText(String.format("%.2f", mPref.getFloat("era_calib", (float)0.4)));
			// 0.00 to 1.00 scale
			dlgSeekBar.setMax(100);
			dlgSeekBar.incrementProgressBy(1);
			dlgSeekBar.setProgress((int)(Float.parseFloat(String.format("%.2f", mPref.getFloat("era_calib", (float)0.4))) * 100));
			
			// seekbar callback
			dlgSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					if (seekBar.getProgress() < 1) {
						dlgText.setText("0");
						seekBar.setProgress(0);
					} else
						dlgText.setText(""+((float)seekBar.getProgress()/100));
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					dlgText.setText(""+((float)seekBar.getProgress()/100));
				}
			});
			
			dlgButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Save current setting then close
					editor.putFloat("era_calib", Float.parseFloat(dlgText.getText().toString())).commit();
					new NativeTask().execute(0);
					dlgRefine.dismiss();
				}
			});
			dlgRefine.show();
    	}
    	else if(pref == mReset)
    	{
    		new AlertDialog.Builder(getActivity())
    		.setIcon(android.R.drawable.ic_dialog_alert)
    		.setTitle(R.string.dlg_title_reset)
    		.setMessage(R.string.dlg_content_reset)
    		.setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
	    			// reset shared preferences
    				editor.clear().commit();
    			}
    		})
    		.setNegativeButton(R.string.dlg_no, null)
    		.show();
    	}
    	return false;
    }
private class NativeTask extends AsyncTask<Integer, Void, Boolean> {
	ProgressDialog pDialog;

	@Override
	protected void onPreExecute() {
		// Opens dialog on loading data file to external storage
		super.onPreExecute();
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Saving");
		pDialog.setIndeterminate(true);
		pDialog.setCancelable(false);
		pDialog.show();
	}
		@Override
		protected Boolean doInBackground(Integer... arg) {
			int mode = arg[0];
			// Make binary Data file example - Image Correction example
			// if u want make DYSCHROMATOPSA Data file, insert factor & mode = 1
			// mode 0 : Image Correction
			// mode 1 : Image DYSCHROMATOPSA
			float native_factor = mPref.getFloat((mode>0? "era_calib" : "inverse_calib"), (float) 0.4);
			era.MakeTreeFile(10, native_factor, "/sdcard/Pictures/ERA/ImageCorrectionData.bin", mode);
			
			return true;
		}
		protected void onPostExecute(Boolean isDone) {
			pDialog.dismiss();
			
			if(!isDone){
				Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
