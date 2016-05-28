package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quitarts.cellfense.R;
import com.quitarts.cellfense.torefactor.Utils;

/**
 * Display art images based on unlocked levels. Trophy.
 */
public class ArtActivity extends Activity {
    private int imageCounter = 0;
    private int imageCounterLimit;
    private RelativeLayout layoutContainer;
    private TextView textViewImageCounter;
    private int[] idsImages = {
            R.drawable.art_01, R.drawable.art_02, R.drawable.art_03, R.drawable.art_04,
            R.drawable.art_05, R.drawable.art_06, R.drawable.art_07, R.drawable.art_08,
            R.drawable.art_09, R.drawable.art_10, R.drawable.art_11, R.drawable.art_12,
            R.drawable.art_13, R.drawable.art_14, R.drawable.art_15, R.drawable.art_16,
            R.drawable.art_17, R.drawable.art_18, R.drawable.art_19, R.drawable.art_20
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_art);

        init();
        initViews();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (imageCounter < imageCounterLimit - 1)
                imageCounter++;
            else
                imageCounter = 0;

            updateUi();
        }

        return true;
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        imageCounterLimit = Integer.valueOf(sharedPreferences.getString(Utils.UNLOCKED_ART, "0"));
    }

    private void initViews() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/tseries_c.ttf");

        layoutContainer = (RelativeLayout) findViewById(R.id.art_layout_container);

        textViewImageCounter = (TextView) findViewById(R.id.art_textview_image_counter);
        textViewImageCounter.setTypeface(typeface);

        updateUi();
    }

    private void updateUi() {
        layoutContainer.setBackgroundDrawable(getResources().getDrawable(idsImages[imageCounter]));
        textViewImageCounter.setText(String.valueOf(imageCounter + 1) + "/" + String.valueOf(imageCounterLimit));
    }
}