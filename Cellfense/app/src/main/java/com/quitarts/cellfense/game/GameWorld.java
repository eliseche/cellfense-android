package com.quitarts.cellfense.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;

public class GameWorld {
    private GameControl gameControl;
    private int width;
    private int height;
    private Bitmap background;
    private int offSetY;
    private int deltaPositionY;

    public GameWorld(int width, int height, GameControl gameControl) {
        this.width = width;
        this.height = height * 2;
        this.gameControl = gameControl;
        background = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.screen_background),
                this.width, this.height, false);
    }

    public void update(int dt) {
        processOffsetY();
    }

    private void processOffsetY() {
        offSetY += deltaPositionY;
        if (offSetY < 0)
            offSetY = 0;
        else if (offSetY > height / 2)
            offSetY = height / 2;
        Utils.setOffsetY(offSetY);
    }

    public void slideToTopScreen() {
        deltaPositionY = -45;
    }

    public void slideToBottomScreen() {
        deltaPositionY = 45;
    }

    public void slideToTopScreen(int speed) {
        deltaPositionY = -speed;
    }

    public void drawWorld(Canvas canvas) {
        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawBitmap(background, 0, -offSetY, null);
    }
}