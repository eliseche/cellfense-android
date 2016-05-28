package com.quitarts.cellfense.game.object.base;

import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;
import android.graphics.drawable.BitmapDrawable;

public class GraphicObject implements Cloneable {
	private BitmapDrawable bitmap;
	public float x = 0;
	public float y = 0;
	public float xCenter = 0;
	public float yCenter = 0;
	
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