package com.quitarts.cellfense;

import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;

public class Bullet extends MovableTileAnimation {
	public Bullet(DrawableType drawableType, int tileRows, int tileColumns, int frameSkipDelay) {
		super(drawableType, tileRows, tileColumns, frameSkipDelay, true);				
	}
}
