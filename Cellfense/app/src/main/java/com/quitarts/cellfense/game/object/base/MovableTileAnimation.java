package com.quitarts.cellfense.game.object.base;

import com.quitarts.cellfense.game.FactoryDrawable;

public class MovableTileAnimation extends TileAnimation {
    private float speedX;
    private float speedY;
    private int directionX;
    private int directionY;

    public MovableTileAnimation(FactoryDrawable.DrawableType drawableType, int rows, int columns, int frameSkipDelay, boolean repeatAnimation) {
        super(drawableType, rows, columns, frameSkipDelay, repeatAnimation);
    }

    public void setSpeed(float speedX, float speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
        this.speedX = this.speedY;
    }

    public void setDirection(int directionX, int directionY) {
        this.directionX = directionX;
        this.directionY = directionY;
    }

    public int[] getDirection() {
        int[] direction = {directionX, directionY};
        return direction;
    }

    public void advance(int dt) {
        // Convert dt to seconds
        float dtSeg = dt / 1000.0f;
        float incX = 0;
        float incY = 0;

        incX = directionX * dtSeg * speedX;
        incY = directionY * dtSeg * speedY;

        setX(getX() + incX);
        setY(getY() + incY);
    }
}


