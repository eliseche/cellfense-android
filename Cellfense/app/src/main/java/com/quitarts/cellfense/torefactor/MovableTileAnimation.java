package com.quitarts.cellfense.torefactor;

import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;

public class MovableTileAnimation extends TileAnimation {
	private float speedX;
	private float speedY;
	private int movX;
	private int movY;

	public MovableTileAnimation(DrawableType drawableType, int tileRows,
			int tileColumns, int frameSkipDelay, boolean repeatAnimation) {
		super(drawableType, tileRows, tileColumns, frameSkipDelay,
				repeatAnimation);
	}

	public void setAdvanceDirection(int velX, int velY) {
		this.movX = velX;
		this.movY = velY;
	}

	public void advance(int dt) {
		// Convert dt to seconds
		float dtSeg = dt / 1000.0f;
		float incX = 0;
		float incY = 0;
		
		incX = (float) (movX * (dtSeg * speedX));
		incY = (float) (movY * (dtSeg * speedY));

		
		setX(getX() + incX);
		setY(getY() + incY);

	}

	public void setSpeedPercentageXY(float spX, float spY) {
		speedX = Utils.getCellSize() * spX;
		speedY = Utils.getCellSize() * spY;
	}

	public float getSpeedX() {
		return speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedPixel(float speedPixelX, float speedPixelY) {
		speedX = speedPixelX;
		speedY = speedPixelY;
	}

	public void setSpeedToVerticalValue(float verticalSpeed) {
		speedY = Utils.getCellSize() * verticalSpeed;
		speedX = speedY;
	}

	public int[] getDirection() {
		int[] directions = { movX, movY };
		return directions;
	}
}


