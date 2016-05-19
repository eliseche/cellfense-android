package com.quitarts.cellfense;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Tutorial extends Activity {
	private LinearLayout ll;
	private int unlockedLimit;
	private int counter = 0;
	private TextView tv;
	
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
        ContextContainer.setApplicationContext(this);
        SharedPreferences sp = ContextContainer.getApplicationContext().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        unlockedLimit =  Integer.valueOf(sp.getString(Utils.UNLOCKED_ART, "0"));
        initLayout();       
        super.setContentView(ll);
       
     
    }

	private void initLayout() {
		ll = new LinearLayout(this);        
        ll.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        ll.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(idsImages[counter]));
        tv = new TextView(ContextContainer.getApplicationContext());
        Typeface tfText = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/tseries_c.ttf");
        tv.setTypeface(tfText);
        tv.setTextSize(25*Utils.getScaleFactor());
        tv.setTextColor(Color.GRAY);
        tv.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        tv.setText(String.valueOf(counter+1) + "/" + String.valueOf(unlockedLimit));
        tv.setGravity(Gravity.BOTTOM|Gravity.CENTER); 
        ll.addView(tv);
        
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:				
			if(counter < unlockedLimit -1) {
				counter++;								
			}
			else {
				counter = 0;
			}
			ll.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(idsImages[counter]));
			
			 if(tv != null)
				 tv.setText(String.valueOf(counter+1) + "/" + String.valueOf(unlockedLimit));
			break;
		case MotionEvent.ACTION_MOVE:			
			break;
		case MotionEvent.ACTION_UP:			
			break;	
		default:
			break;
    	}
    	return true;
	}
}