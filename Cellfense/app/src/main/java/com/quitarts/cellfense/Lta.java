package com.quitarts.cellfense;

import com.quitarts.cellfense.FactoryDrawable.DrawableType;

public class Lta extends TileAnimation {
	
	private int damage = GameRules.getLtaDamage();
	
	public Lta(DrawableType drawableType, int tileRows, int tileColumns, int frameSkipDelay) {
		super(drawableType, tileRows, tileColumns, frameSkipDelay,true);
	}
	
	public boolean isClicked(int x, int y) {
		if(x > (getX() - getWidth()) && x < (getX() + getWidth() * 2) &&
		   y > (getY() - getHeight()) && y < (getY() + getHeight() * 2))
			return true;
		return false;
	}
	
	public int getDamage(){
		return damage;
	}
	
	public void updateLTA(){
		damage = (int) (damage + damage * 0.1f);
	}
}