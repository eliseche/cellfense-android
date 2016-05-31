package com.quitarts.cellfense.game.object;

import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.object.base.MovableTileAnimation;

public class Bullet extends MovableTileAnimation {
    public Bullet(FactoryDrawable.DrawableType drawableType, int rows, int columns, int frameSkipDelay) {
        super(drawableType, rows, columns, frameSkipDelay, true); // true to repeat animation
    }
}