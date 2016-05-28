package com.quitarts.cellfense.torefactor;

import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;

public class Button extends GraphicObject {		
	public Button(DrawableType drawableType) {
		super(drawableType);		
	}
	
	public boolean isClicked(int x, int y) {		
		if(x > getX() && x < (getX() + getWidth()) && 
		   y > getY() && y < (getY() + getHeight()))
			return true;			
		return false;
	}
}