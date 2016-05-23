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

        // Load custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/apexnew_medium.ttf");

        // Set font
        // TODO: We can get root view, get children views and set font
        TextView textViewDevelopedBy = (TextView) findViewById(R.id.about_developed_by);
        textViewDevelopedBy.setTypeface(font);

        TextView textViewAuthor01 = (TextView) findViewById(R.id.about_author01);
        textViewAuthor01.setTypeface(font);

        TextView textViewAuthor02 = (TextView) findViewById(R.id.about_author02);
        textViewAuthor02.setTypeface(font);

        TextView textViewAuthor03 = (TextView) findViewById(R.id.about_author03);
        textViewAuthor03.setTypeface(font);

        TextView textViewAuthor04 = (TextView) findViewById(R.id.about_author04);
        textViewAuthor04.setTypeface(font);

        TextView textViewArtBy = (TextView) findViewById(R.id.about_art_by);
        textViewArtBy.setTypeface(font);

        TextView textViewAuthor05 = (TextView) findViewById(R.id.about_author05);
        textViewAuthor05.setTypeface(font);

        TextView textViewAuthor06 = (TextView) findViewById(R.id.about_author06);
        textViewAuthor06.setTypeface(font);

        TextView textViewMusicBy = (TextView) findViewById(R.id.about_music_by);
        textViewMusicBy.setTypeface(font);

        TextView textViewVersion = (TextView) findViewById(R.id.about_version);
        textViewVersion.setTypeface(font);
    }
}