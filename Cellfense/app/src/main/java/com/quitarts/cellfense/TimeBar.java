package com.quitarts.cellfense;

import android.graphics.Canvas; 
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class TimeBar {
	private Paint timeBarPaint;
	private Rect timeBarRect;
	private int accumDt;
	private int timeBarPercentageScreenHeight;
	private boolean active;	
	private int timeBar;
	
	public TimeBar(boolean act) {
		active = act;
		timeBarPaint = new Paint();			
		timeBarPaint.setColor(Color.rgb(0, 255, 0));		
		timeBarPaint.setAlpha(100);
		timeBarRect = new Rect();	
		timeBarPercentageScreenHeight = 1;
		timeBarRect.set(0, 0, Utils.getCanvasWidth(), timeBarPercentageScreenHeight*Utils.getCanvasHeight()/100);
		
		if(active)
			timeBar = GameRules.getTimeBar();
		else
			timeBar = GameRules.getTimeBar()*1000;
	}
	
	public void draw(Canvas c) {
		if(active)
			c.drawRect(timeBarRect, timeBarPaint);
	}

	public void update(int dt) {
		int timeBarPercentageAdvance;
		int timeBarPixelAdvance;
		
		accumDt += dt;
	
		timeBarPixelAdvance = accumDt*Utils.getCanvasWidth()/timeBar;
		timeBarPercentageAdvance = timeBarPixelAdvance*100/Utils.getCanvasWidth();
		
		if(timeBarPercentageAdvance >= 50 && timeBarPercentageAdvance <= 80){
			timeBarPaint.setColor(Color.argb(150,255,255, 0));		
		}
		else if(timeBarPercentageAdvance > 80){			
			timeBarPaint.setColor(Color.argb(150,255,0, 0));		
		}		
		else{
			timeBarPaint.setColor(Color.argb(150,0,255, 0));			
		}
		
		if(accumDt <= timeBar){

			timeBarRect.set(0, 0, Utils.getCanvasWidth() - timeBarPixelAdvance,  timeBarPercentageScreenHeight*Utils.getCanvasHeight()/100);	
		}	
		else
			timeBarRect.set(0, 0, 0, 5);
	}
	
	public boolean isTimeOver() {		
		if(timeBarRect.right <= 0) {
			accumDt = 0;
			return true;			
		}				
		else {
			return false;
		}		
	}	
	
	public int finishCount(){
		int timeToFinish;
		timeToFinish = timeBar - accumDt;
		accumDt = timeBar;
		return timeToFinish;
	}
	
	public void restart() {
		timeBarRect.right = Utils.getCanvasWidth();
		accumDt = 0;
	}
}