package com.quitarts.cellfense.game.object;

import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.object.base.GraphicObject;

public class Button extends GraphicObject {
    public Button(FactoryDrawable.DrawableType drawableType) {
        super(drawableType);
    }

    // Return true (clicked) if clicked inside of the drawable
    public boolean isClicked(int x, int y) {
        if (x > getX() && x < (getX() + getWidth()) && y > getY() && y < (getY() + getHeight()))
            return true;

        return false;
    }
}