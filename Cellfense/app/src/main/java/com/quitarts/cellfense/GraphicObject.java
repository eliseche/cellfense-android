package com.quitarts.cellfense;

import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;
import android.graphics.drawable.BitmapDrawable;

public class GraphicObject implements Cloneable {
	private BitmapDrawable bitmap;
	protected float x = 0;
	protected float y = 0;
	protected float xCenter = 0;
	protected float yCenter = 0;
	
	public GraphicObject(DrawableType drawableType) {
		bitmap = FactoryDrawable.createDrawable(drawableType);
	}
	
	public BitmapDrawable getGraphic() {
		return bitmap;
	}	
	
	public void setGraphicObject(DrawableType drawableType){
		bitmap = FactoryDrawable.createDrawable(drawableType);	
	}
	
	public float getX() {
		return x;				
	}
		
	public void setX(float value) {
		x = value;
		recalcCenter();
	}
	
	public float getXcenter() {
		return xCenter;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float value) {
		y = value;
		recalcCenter();
	}	
	
	public float getYcenter() {
		return yCenter;
	}
	
	public int getWidth() {		
		return bitmap.getMinimumWidth();		
	}
	
	public int getHeight() {
		return bitmap.getMinimumHeight();
	}
	
	private void recalcCenter() {
		xCenter = x + getWidth() / 2.0f;
		yCenter = y + getHeight() / 2.0f;
	}	
	
	public Object Clone() {		
		try {			
			GraphicObject cloneGraphicObject = (GraphicObject)super.clone();
			return cloneGraphicObject;
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}