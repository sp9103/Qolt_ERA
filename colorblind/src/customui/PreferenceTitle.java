package customui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PreferenceTitle extends PreferenceCategory {

	public PreferenceTitle(Context context) {
		super(context);
	}

	public PreferenceTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PreferenceTitle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void onBindView(View view){
		super.onBindView(view);
		TextView titleView = (TextView)view.findViewById(android.R.id.title);
		titleView.setTextColor(0xff33b5e5);
	}
}

