package com.quitarts.cellfense.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Button;

public class Hud {
    private GameControl gameControl;
    private Rect hud;
    private Button buttonHudArrow;

    public Hud(GameControl gameControl) {
        this.gameControl = gameControl;

        initialize();
    }

    // region Update hud
    public void update(int dt) {
        ;
    }
    // endregion

    // region Draw hud
    public void draw(Canvas canvas) {
        drawHud(canvas);
        drawHudArrow(canvas);
    }

    private void drawHud(Canvas canvas) {
        Paint hudPaint = new Paint();
        hudPaint.setStyle(Paint.Style.FILL);
        hudPaint.setARGB(180, 0, 0, 0);

        canvas.drawRect(hud, hudPaint);
    }

    public void drawHudArrow(Canvas canvas) {
        buttonHudArrow.setX(0);
        buttonHudArrow.setY(Utils.getCanvasHeight() - buttonHudArrow.getHeight());
        buttonHudArrow.getGraphic().setBounds((int) buttonHudArrow.getX(), (int) buttonHudArrow.getY(), (int) buttonHudArrow.getX() + buttonHudArrow.getWidth(), (int) buttonHudArrow.getY() + buttonHudArrow.getHeight());
        buttonHudArrow.getGraphic().draw(canvas);
    }
    // endregion

    // region Events
    public boolean buttonHudArrowClicked(int x, int y) {
        if (buttonHudArrow.isClicked(x, y))
            return true;

        return false;
    }
    // endregion

    private void initialize() {
        hud = new Rect(0, Utils.getCanvasHeight() - (int) Utils.getCellHeight(), Utils.getCanvasWidth(), (int) Utils.getCanvasHeight());
        buttonHudArrow = new Button(FactoryDrawable.DrawableType.HUD_ARROW);
    }
}