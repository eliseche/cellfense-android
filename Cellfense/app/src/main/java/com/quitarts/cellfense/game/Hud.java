package com.quitarts.cellfense.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Button;

public class Hud {
    private GameControl gameControl;
    private Rect hud;
    private Paint hudPaint;
    private Button buttonHudArrow;

    public Hud(GameControl gameControl) {
        this.gameControl = gameControl;

        initialize();
    }

    // region Update hud
    public void update(int dt) {
    }
    // endregion

    // region Draw hud
    public void drawBaseHud(Canvas canvas) {
        drawHudArrow(canvas);
    }

    public void drawBottomHud(Canvas canvas) {
        drawHud(canvas);
    }

    private void drawHud(Canvas canvas) {
        canvas.drawRect(hud, hudPaint);
    }

    public void drawHudArrow(Canvas canvas) {
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
        // HudArrow
        buttonHudArrow = new Button(FactoryDrawable.DrawableType.HUD_ARROW);
        buttonHudArrow.setX(0);
        buttonHudArrow.setY(Utils.getCanvasHeight() - buttonHudArrow.getHeight());

        // Hud
        hud = new Rect(0, Utils.getCanvasHeight() - buttonHudArrow.getHeight(), Utils.getCanvasWidth(), Utils.getCanvasHeight());
        hudPaint = new Paint();
        hudPaint.setStyle(Paint.Style.FILL);
        hudPaint.setARGB(180, 0, 0, 0);
    }
}