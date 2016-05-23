package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.quitarts.cellfense.R;

public class AboutActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        initViews();
    }

    private void initViews() {
        // Load custom font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/apexnew_medium.ttf");

        // Set font
        // TODO: We can get root view, get children views and set font
        TextView textViewDevelopedBy = (TextView) findViewById(R.id.about_developed_by);
        textViewDevelopedBy.setTypeface(typeface);

        TextView textViewAuthor01 = (TextView) findViewById(R.id.about_author01);
        textViewAuthor01.setTypeface(typeface);

        TextView textViewAuthor02 = (TextView) findViewById(R.id.about_author02);
        textViewAuthor02.setTypeface(typeface);

        TextView textViewAuthor03 = (TextView) findViewById(R.id.about_author03);
        textViewAuthor03.setTypeface(typeface);

        TextView textViewAuthor04 = (TextView) findViewById(R.id.about_author04);
        textViewAuthor04.setTypeface(typeface);

        TextView textViewArtBy = (TextView) findViewById(R.id.about_art_by);
        textViewArtBy.setTypeface(typeface);

        TextView textViewAuthor05 = (TextView) findViewById(R.id.about_author05);
        textViewAuthor05.setTypeface(typeface);

        TextView textViewAuthor06 = (TextView) findViewById(R.id.about_author06);
        textViewAuthor06.setTypeface(typeface);

        TextView textViewMusicBy = (TextView) findViewById(R.id.about_music_by);
        textViewMusicBy.setTypeface(typeface);

        TextView textViewVersion = (TextView) findViewById(R.id.about_version);
        textViewVersion.setTypeface(typeface);
    }
}