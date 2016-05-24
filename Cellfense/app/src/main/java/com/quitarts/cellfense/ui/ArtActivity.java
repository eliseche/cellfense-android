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
import com.quitarts.cellfense.Utils;

public class ArtActivity extends Activity {
    private int imageCounter = 0;
    private int imageCounterLimit;
    private RelativeLayout layoutContainer;
    private TextView textViewImageCounter;

    private int[] idsImages = {
            R.drawable.art01, R.drawable.art02, R.drawable.art03, R.drawable.art04,
            R.drawable.art05, R.drawable.art06, R.drawable.art07, R.drawable.art08,
            R.drawable.art09, R.drawable.art10, R.drawable.art11, R.drawable.art12,
            R.drawable.art13, R.drawable.art14, R.drawable.art15, R.drawable.art16,
            R.drawable.art17, R.drawable.art18, R.drawable.art19, R.drawable.art20
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_art);

        init();
        initViews();
        ;
    }

    private void init() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        imageCounterLimit = Integer.valueOf(sharedPreferences.getString(Utils.UNLOCKED_ART, "0"));
    }

    private void initViews() {
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/tseries_c.ttf");

        layoutContainer = (RelativeLayout) findViewById(R.id.art_layout_container);

        textViewImageCounter = (TextView) findViewById(R.id.art_textview_image_counter);
        textViewImageCounter.setTypeface(typeface);

        updateUi();
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

    private void updateUi() {
        layoutContainer.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(idsImages[imageCounter]));
        textViewImageCounter.setText(String.valueOf(imageCounter + 1) + "/" + String.valueOf(imageCounterLimit));
    }
}