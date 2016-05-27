package com.quitarts.cellfense;

import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;

public class MovableGraphicObject extends GraphicObject {	
	private float unitTime = 10;
	private float velX;
	private float velY;
	
	public MovableGraphicObject(DrawableType drawableType) {
		super(drawableType);		
	}
	
	public void setVelocity(float velX, float velY) {
		this.velX = velX;
		this.velY = velY;
	}
	
	public void advance(int dt) {
		float incX = 0;
		float incY = 0;
		
		incX = velX * dt / unitTime;
		incY = velY * dt / unitTime;		
		setX(getX() + incX);
		setY(getY() + incY);		
	}
}