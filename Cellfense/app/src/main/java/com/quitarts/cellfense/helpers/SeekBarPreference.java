package com.quitarts.cellfense.helpers;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class SeekBarPreference extends DialogPreference {
    private Context context;
    private SeekBar seekBar;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        seekBar = new SeekBar(context);
        seekBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        seekBar.setMax(100);
        seekBar.setProgress(getSharedPreferences().getInt(getKey(), 100));

        layout.addView(seekBar);
        builder.setView(layout);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            persistInt(seekBar.getProgress());
    }
}