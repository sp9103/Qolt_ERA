package customui;

import android.content.Context;
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
	protected View onCreateView(ViewGroup parent) {
		// And it's just a TextView!
		TextView categoryTitle =  (TextView)super.onCreateView(parent);
		categoryTitle.setTextColor(Color.GRAY);

		return categoryTitle;
	}
}

