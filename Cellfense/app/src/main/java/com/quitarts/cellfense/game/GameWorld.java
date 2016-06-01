package com.quitarts.cellfense.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;

public class GameWorld {
    private int width;
    private int height;
    private GameControl gameControl;
    private Bitmap background;
    private int offsetY;
    private int deltaPositionY;

    public GameWorld(int width, int height, GameControl gameControl) {
        this.width = width;
        this.height = height * 2;
        this.gameControl = gameControl;
        background = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.screen_background),
                this.width, this.height, false);
    }

    // region Update world
    public void update(int dt) {
        processOffsetY();
    }

    // Update offsetY value to be used to slide background
    private void processOffsetY() {
        offsetY += deltaPositionY;
        if (offsetY < 0)
            offsetY = 0;
        else if (offsetY > height / 2)
            offsetY = height / 2;
    }
    // endregion

    // region Draw world
    public void drawWorld(Canvas canvas) {
        drawBackground(canvas);
    }

    // Draw backgorund image, slide it based on offsetY
    private void drawBackground(Canvas canvas) {
        canvas.drawBitmap(background, 0, -offsetY, null);
    }
    // endregion

    public void slideToTopScreen() {
        deltaPositionY = -45;
    }

    public void slideToBottomScreen() {
        deltaPositionY = 45;
    }
}