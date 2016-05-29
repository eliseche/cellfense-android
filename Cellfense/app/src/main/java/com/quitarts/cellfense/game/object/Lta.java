package com.quitarts.cellfense.game.object;

import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.object.base.TileAnimation;

public class Lta extends TileAnimation {
    public Lta(FactoryDrawable.DrawableType drawableType, int rows, int columns, int frameSkipDelay) {
        super(drawableType, rows, columns, frameSkipDelay, true); // true to repeat animation
    }

    // Return true (clicked) if clicked inside the double size of the drawable
    public boolean isClicked(int x, int y) {
        if (x > (getX() - getWidth()) && x < (getX() + getWidth() * 2) &&
                y > (getY() - getHeight()) && y < (getY() + getHeight() * 2))
            return true;

        return false;
    }
}


