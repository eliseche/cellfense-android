package com.quitarts.cellfense;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/apexnew_medium.ttf");		
		TextView developed = (TextView) findViewById(R.id.developed);		
		developed.setTypeface(font);
		TextView author01 = (TextView) findViewById(R.id.author01);
		author01.setTypeface(font);		
		TextView author02 = (TextView) findViewById(R.id.author02);
		author02.setTypeface(font);
		TextView author03 = (TextView) findViewById(R.id.author03);
		author03.setTypeface(font);
		TextView author04 = (TextView) findViewById(R.id.author04);
		author04.setTypeface(font);
		TextView art = (TextView) findViewById(R.id.artBy);
		art.setTypeface(font);
		TextView author05 = (TextView) findViewById(R.id.author05);
		author05.setTypeface(font);
		TextView author06 = (TextView) findViewById(R.id.author06);
		author06.setTypeface(font);
		TextView music = (TextView) findViewById(R.id.music);
		music.setTypeface(font);
		TextView version = (TextView) findViewById(R.id.version);
		version.setTypeface(font);
	}
}