package customui;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.naubull2.colorblind.R;

public class SettingFragment extends PreferenceFragment implements OnPreferenceClickListener{
	private Preference mNotice, mVersion, mAbout, mHelp, mExpValue, mEraValue, mInterval, mReset;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_setting);
        
        mNotice = (Preference)findPreference("key_notice");
        mVersion = (Preference)findPreference("key_version");
        mAbout = (Preference)findPreference("key_about");
        mHelp = (Preference)findPreference("key_help");
        
        mExpValue = (Preference)findPreference("key_exp_value");
        mEraValue = (Preference)findPreference("key_era_value");
        mInterval = (Preference)findPreference("key_interval");
        mReset = (Preference)findPreference("key_reset");

        mNotice.setOnPreferenceClickListener(this);
        mVersion.setOnPreferenceClickListener(this);
        mAbout.setOnPreferenceClickListener(this);
        mHelp.setOnPreferenceClickListener(this);
        
        mExpValue.setOnPreferenceClickListener(this);
        mEraValue.setOnPreferenceClickListener(this);
        mInterval.setOnPreferenceClickListener(this);
        mReset.setOnPreferenceClickListener(this);
    }

	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}
}
