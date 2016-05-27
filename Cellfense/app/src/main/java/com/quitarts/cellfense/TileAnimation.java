package com.quitarts.cellfense;

import java.util.ArrayList;
import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class TileAnimation extends GraphicObject {		
	private int tileRows;
	private int tileColumns;
	private int frameSkipDelay;
	
	private ArrayList<BitmapDrawable> imageList = new ArrayList<BitmapDrawable>();
	protected int currentFrame = 0;
	private int accumTime;
	private boolean isStarted = false;	
	private int stepX;
	private int stepY;
	private boolean repeatAnimation;
	public int rotAngle;
		
	public TileAnimation(DrawableType drawableType, int tileRows, int tileColumns, int frameSkipDelay, boolean repeatAnimation) {
		super(drawableType);		
		this.tileRows = tileRows;
		this.tileColumns = tileColumns;
		this.frameSkipDelay = frameSkipDelay;
		this.repeatAnimation = repeatAnimation;
		if(repeatAnimation == true) {
			this.start();
		}
		
		cutImage();		
	}		
	
	public BitmapDrawable getGraphic() {
		return imageList.get(currentFrame);
	}
	
	public ArrayList<BitmapDrawable> getGraphics() {
		return imageList;
	}
	
	public void setTileAnimation(DrawableType drawableType, int tileRows, int tileColumns, int frameSkipDelay, boolean repeatAnimation) {
		setGraphicObject(drawableType);
		this.tileRows = tileRows;
		this.tileColumns = tileColumns;
		this.frameSkipDelay = frameSkipDelay;
		this.repeatAnimation = repeatAnimation;
		if(repeatAnimation == true) {
			this.start();
		}
		imageList.clear();
		cutImage();		
	}
		
	public void setX(float value) {
		x = value;
		recalcCenter();
	}
		
	public void setY(float value) {
		y = value;
		recalcCenter();
	}	
	
	public int getWidth() {		
		return imageList.get(currentFrame).getMinimumWidth();		
	}
	
	public int getHeight() {
		return imageList.get(currentFrame).getMinimumHeight();
	}
	
	private void recalcCenter() {
		xCenter = x + getWidth() / 2.0f;
		yCenter = y + getHeight() / 2.0f;
	}
	
	public void tileAnimationUpdate(int dt) {
		if(isStarted) {	
			accumTime += dt;			
			if(accumTime >= frameSkipDelay) {
				accumTime = 0;
				nextFrame();
			}
		}
	}
	
	public void start() {
		isStarted = true;		
	}
	
	public void stop() {
		isStarted = false;		
	}
	
	public void stopAndResetFrame() {
		isStarted = false;
		currentFrame = 0;
	}
	
	private void cutImage() {
		int offsetX = 0;
		int offsetY = 0;
		stepX = super.getGraphic().getMinimumWidth() / tileColumns;
		stepY = super.getGraphic().getMinimumHeight() / tileRows;		
		
		for(int i = 0; i < tileRows; i++) {
			for(int j = 0; j < tileColumns; j++) {				
				if(offsetX + stepX <= super.getGraphic().getMinimumWidth()) {
					Bitmap tmpBitmap = Bitmap.createBitmap(super.getGraphic().getBitmap(), offsetX, offsetY, stepX, stepY);
					BitmapDrawable tmpBitmapDrawable = new BitmapDrawable(ContextContainer.getApplicationContext().getResources(), tmpBitmap); 
					imageList.add(tmpBitmapDrawable);
					offsetX += stepX;
				}
			}
			offsetX = 0;
			if(offsetY + stepY <= super.getGraphic().getMinimumHeight()) {
				offsetY += stepY;
			}
		}		
	}	
		
	private void nextFrame() {			
		if(currentFrame < imageList.size() -1) {
			currentFrame++;
		}
		else {
			currentFrame = 0;
			if(!repeatAnimation)
				isStarted = false;
		}		
	}	
}