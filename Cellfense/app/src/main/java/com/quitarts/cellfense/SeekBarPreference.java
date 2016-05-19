package com.quitarts.cellfense;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class SeekBarPreference extends DialogPreference {
	private Context context;
	private SeekBar volumeLevel;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		layout.setPadding(20, 20, 20, 20);		

		volumeLevel = new SeekBar(context);
		volumeLevel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		volumeLevel.setMax(100);
		volumeLevel.setProgress(getSharedPreferences().getInt(getKey(), 100));						

		layout.addView(volumeLevel);
		builder.setView(layout);		
	}

	protected void onDialogClosed(boolean positiveResult) {
		if(positiveResult) {
			persistInt(volumeLevel.getProgress());
		}
	}
}