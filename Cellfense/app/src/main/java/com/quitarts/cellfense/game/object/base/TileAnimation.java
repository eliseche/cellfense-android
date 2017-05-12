package com.quitarts.cellfense.game.object.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.game.FactoryDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Load animated sprite
 */
public class TileAnimation extends GraphicObject {
    private List<GraphicObject> tiles = new ArrayList<>();
    private int rows;
    private int columns;
    private int frameSkipDelay;
    private boolean repeatAnimation;
    private int currentFrame = 0;
    private boolean isStarted = false;
    private int accumTime;
    private int rotationAngle = 0;

    public TileAnimation(FactoryDrawable.DrawableType drawableType, int rows, int columns, int frameSkipDelay, boolean repeatAnimation) {
        super(drawableType);

        this.rows = rows;
        this.columns = columns;
        this.frameSkipDelay = frameSkipDelay;
        this.repeatAnimation = repeatAnimation;

        generateTiles();
    }

    public void setTileAnimation(FactoryDrawable.DrawableType drawableType, int rows, int columns, int frameSkipDelay, boolean repeatAnimation) {
        tiles.clear();
        setGraphic(drawableType);

        this.rows = rows;
        this.columns = columns;
        this.frameSkipDelay = frameSkipDelay;
        this.repeatAnimation = repeatAnimation;

        generateTiles();
    }

    public BitmapDrawable getGraphic() {
        return tiles.get(currentFrame).getGraphic();
    }

    public List<GraphicObject> getGraphics() {
        return tiles;
    }

    @Override
    public int getWidth() {
        return tiles.get(currentFrame).getWidth();
    }

    @Override
    public int getHeight() {
        return tiles.get(currentFrame).getHeight();
    }

    @Override
    public void setX(float x) {
        super.x = x;
        calculateCenter();
        calculateBounds();
    }

    @Override
    public void setY(float y) {
        super.y = y;
        calculateCenter();
        calculateBounds();
    }

    @Override
    public void calculateCenter() {
        super.xCenter = super.x + getWidth() / 2.0f;
        super.yCenter = super.y + getHeight() / 2.0f;
    }

    @Override
    public void calculateBounds() {
        for (int i = 0; i < tiles.size(); i++)
            tiles.get(i).getGraphic().setBounds((int) super.x, (int) super.y, (int) super.getX() + tiles.get(i).getWidth(), (int) super.getY() + tiles.get(i).getHeight());
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public void start() {
        isStarted = true;
    }

    public void stop() {
        isStarted = false;
    }

    public void reset() {
        currentFrame = 0;
    }

    public void stopAndReset() {
        stop();
        reset();
    }

    // Update tile (nextFrame) in {dt} time
    public void updateTile(int dt) {
        if (isStarted) {
            accumTime += dt;
            if (accumTime >= frameSkipDelay) {
                accumTime = 0;
                nextFrame();
            }
        }
    }

    private void nextFrame() {
        if (currentFrame < tiles.size() - 1)
            currentFrame++;
        else {
            currentFrame = 0;
            if (!repeatAnimation)
                isStarted = false;
        }
    }

    // Create tiles (List<BitmapDrawable>) by splitting the whole image in columns and rows
    private void generateTiles() {
        int offsetX = 0;
        int offsetY = 0;
        int stepX = super.getWidth() / columns;
        int stepY = super.getHeight() / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (offsetX + stepX <= super.getWidth()) {
                    Bitmap bitmap = Bitmap.createBitmap(super.getGraphic().getBitmap(), offsetX, offsetY, stepX, stepY);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(ContextContainer.getContext().getResources(), bitmap);
                    tiles.add(new GraphicObject(bitmapDrawable));
                    offsetX += stepX;
                }
            }

            if (offsetY + stepY <= super.getHeight())
                offsetY += stepY;
        }
    }
}