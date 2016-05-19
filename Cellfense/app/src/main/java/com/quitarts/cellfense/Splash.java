package com.quitarts.cellfense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class Splash extends Activity {
	protected boolean active = true;
	protected int splashTime = 2000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while(active && (waited < splashTime)) {
						sleep(100);
						if(active) {
							waited += 100;							
						}						
					}
				}catch (InterruptedException e) {
				}finally {
					finish();
					startActivity(new Intent(Splash.this, Main.class));					
				}				
			}
		};
		splashThread.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			active = false;
		}
		return true;
	}
}