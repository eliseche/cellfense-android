package com.quitarts.cellfense.game;

import android.graphics.Canvas;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Button;

public class Hud {
    private GameControl gameControl;
    private Button buttonHudArrow;

    public Hud(GameControl gameControl) {
        this.gameControl = gameControl;

        initialize();
    }

    public void draw(Canvas canvas, int dt) {
        drawUpDownHudControl(canvas, dt);
    }

    public void drawUpDownHudControl(Canvas canvas, int dt) {
        buttonHudArrow.setX(0);
        buttonHudArrow.setY(Utils.getCanvasHeight() - (buttonHudArrow.getHeight()));
        buttonHudArrow.getGraphic().setBounds((int) buttonHudArrow.getX(), (int) buttonHudArrow.getY(), (int) buttonHudArrow.getX() + buttonHudArrow.getWidth(), (int) buttonHudArrow.getY() + buttonHudArrow.getHeight());
        buttonHudArrow.getGraphic().draw(canvas);
    }

    public boolean buttonDownClicked(int x, int y) {
        if (buttonHudArrow.isClicked(x, y)) {
            return true;
        }
        return false;
    }

    private void initialize() {
        buttonHudArrow = new Button(FactoryDrawable.DrawableType.HUD_ARROW);
    }
}