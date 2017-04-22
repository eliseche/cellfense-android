package com.quitarts.cellfense.torefactor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;
import com.quitarts.cellfense.game.object.Bullet;
import com.quitarts.cellfense.game.object.base.GraphicObject;
import com.quitarts.cellfense.game.object.base.MovableTileAnimation;
import com.quitarts.pathfinder.Path;
import com.quitarts.pathfinder.Path.Step;

import java.util.List;

public class Critter extends MovableTileAnimation {
    private final int start_live = 100;
    private float lives = start_live;
    private int enerbyBarSize = Utils.GetEnergyBarSize();
    private Rect energyBarRect = new Rect();
    private Paint energyBarPaint = new Paint();
    private int gridPositionX;
    private int gridPositionY;
    private int startGridPosition;
    private int endGridPosition;
    private Path critterPath;
    private Path critterPath2;
    private Path critterPath3;
    private Path critterPath4;
    private Path critterPathWorld = new Path();
    private Path critterPathWorld2 = new Path();
    private Path critterPathWorld3 = new Path();
    private Path critterPathWorld4 = new Path();
    private int nextStepIndex;
    private int accumSlowTime;
    private CritterType type;
    private boolean isSlow;

    public static enum CritterType {
        SPIDER, CATERPILLAR, CHIP
    }

    public Critter(DrawableType drawableType, int tileRows, int tileColumns, int frameSkipDelay) {
        super(drawableType, tileRows, tileColumns, frameSkipDelay, true);
        energyBarPaint.setColor(Color.rgb(43, 180, 9));

        if (drawableType == DrawableType.ENEMY_SPIDER_SPRITE) {
            type = CritterType.SPIDER;
            setDirection(0, 1);
        }
        if (drawableType == DrawableType.ENEMY_CATERPILLAR_SPRITE) {
            type = CritterType.CATERPILLAR;
            setDirection(0, 1);
        }
        if (drawableType == DrawableType.ENEMY_CHIP_INFECTED_SPRITE) {
            type = CritterType.CHIP;
            setDirection(0, 1);
        }
    }

    public CritterType getEnemyType() {
        return type;
    }

    public void advance(int dt, int offSetY) {
        super.advance(dt);
        updateSlowSpeed(dt);

        if (nextStepIndex < this.getCritterWorldPath().getLength()) {
            Step actualStep = getCritterWorldPath().getStep(nextStepIndex);

            //Second Screen
            if (getY() > (Utils.getCanvasHeight() + actualStep.getY() - Utils.getCellSize()) && nextStepIndex == 0) {
                this.setY(actualStep.getY() + offSetY - Utils.getCellSize());
                nextStepIndex++;
                decideDirections(nextStepIndex, nextStepIndex - 1);
                setRotationAngle(0);
                return;
            }
            if (nextStepIndex > 0) {
                if (this.getDirection()[0] == -1) {
                    if (getX() <= actualStep.getX()) {
                        this.setX(actualStep.getX());
                        nextStepIndex++;
                        decideDirections(nextStepIndex, nextStepIndex + 1);
                    }
                    setRotationAngle(90);
                } else if (this.getDirection()[0] == 1) {
                    if (getX() >= actualStep.getX()) {
                        this.setX(actualStep.getX());
                        nextStepIndex++;
                        decideDirections(nextStepIndex, nextStepIndex + 1);
                    }
                    setRotationAngle(-90);
                } else if (this.getDirection()[1] == -1) {
                    if (getY() - offSetY + Utils.getCellSize() < actualStep.getY()) {
                        this.setY(actualStep.getY() + offSetY - Utils.getCellSize());
                        nextStepIndex++;
                        decideDirections(nextStepIndex, nextStepIndex + 1);
                    }
                    setRotationAngle(180);
                } else if (this.getDirection()[1] == 1) {
                    if (getY() - offSetY + Utils.getCellSize() >= actualStep.getY()) {
                        this.setY(actualStep.getY() + offSetY - Utils.getCellSize());
                        nextStepIndex++;
                        decideDirections(nextStepIndex, nextStepIndex + 1);
                    }
                    setRotationAngle(0);
                }
                decideDirections(nextStepIndex, nextStepIndex - 1);
            }
        }
    }

    private void updateSlowSpeed(int dt) {
        if (isSlow) {
            accumSlowTime += dt;
            if (accumSlowTime <= GameRules.getSlowCritterTime()) {
                this.setSpeedY((GameRules.getCrittersStartSpeed(type) * 0.5f) * Utils.getCellSize());
            } else {
                this.setSpeedY(GameRules.getCrittersStartSpeed(type) * Utils.getCellSize());
                accumSlowTime = 0;
                isSlow = false;
                List<GraphicObject> tmpGraphics = this.getGraphics();
                for (GraphicObject go : tmpGraphics) {
                    go.getGraphic().mutate().setColorFilter(null);
                }
            }
        }
    }

    private void decideDirections(int actualIndexStep, int lastIndexStep) {
        if (lastIndexStep >= this.getCritterWorldPath().getLength()) {
            setDirection(0, 1);
            setRotationAngle(0);
        } else if (actualIndexStep >= this.getCritterWorldPath().getLength()) {
            setDirection(0, 1);
            setRotationAngle(0);
        } else {
            Step actualStep = this.getCritterWorldPath().getStep(actualIndexStep);
            Step lastStep = this.getCritterWorldPath().getStep(lastIndexStep);

            if (actualStep.getY() == lastStep.getY()) {
                if (actualStep.getX() > lastStep.getX()) {
                    setDirection(1, 0);
                    return;
                } else if (actualStep.getX() <= lastStep.getX()) {
                    setDirection(-1, 0);
                    return;
                }
            } else {
                if (actualStep.getY() > lastStep.getY()) {
                    setDirection(0, 1);
                    return;
                } else if (actualStep.getY() <= lastStep.getY()) {
                    setDirection(0, -1);
                    return;
                }
            }
            setDirection(0, 1);
            setRotationAngle(0);
        }
    }

    public int getFixXPositionElement() {
        return ((int) getX() / getWidth()) * getWidth();
    }

    public int getFixYPositionElement() {
        if (this.type == CritterType.CATERPILLAR)
            return ((int) getY() / (int) (getHeight() / 1.5f)) * ((int) (getHeight() / 1.5f));
        else
            return ((int) getY() / getHeight()) * getHeight();
    }

    public float lives() {
        return lives;
    }

    public void hit(float damage) {
        if (lives > 0)
            lives -= damage;
    }

    public void getSluggish() {
        accumSlowTime = 0;
        this.isSlow = true;
    }

    public boolean isHit(Bullet bullet) {
        if (getGraphic().getBounds().intersect(bullet.getGraphic().getBounds()))
            return true;
        return false;
    }

    public void setX(float value) {
        super.setX(value);
        gridPositionX = Utils.convertXWorldToGrid(value, getWidth());
    }

    public void setY(float value) {
        super.setY(value);
        gridPositionY = Utils.convertYWorldToGrid(value, getHeight());
    }

    public void setCritterPath(Path path) {
        critterPath = path;
        int value = 0;
        while (value < critterPath.getLength()) {
            critterPathWorld.appendStep(critterPath.getStep(value).getX(), critterPath.getStep(value).getY());
            value++;
        }
        value = 0;
        while (value < critterPathWorld.getLength()) {
            critterPathWorld.getStep(value).setX(Utils.convertXGridToWorld(critterPathWorld.getStep(value).getX(), this.getWidth()));
            critterPathWorld.getStep(value).setY(Utils.convertYGridToWorld(critterPathWorld.getStep(value).getY(), this.getHeight()));
            value++;
        }
    }

    public void setCritterPath2(Path path) {
        critterPath2 = path;
        int value = 0;
        while (value < critterPath2.getLength()) {
            critterPathWorld2.appendStep(critterPath2.getStep(value).getX(), critterPath2.getStep(value).getY());
            value++;
        }
        value = 0;
        while (value < critterPathWorld2.getLength()) {
            critterPathWorld2.getStep(value).setX(Utils.convertXGridToWorld(critterPathWorld2.getStep(value).getX(), this.getWidth()));
            critterPathWorld2.getStep(value).setY(Utils.convertYGridToWorld(critterPathWorld2.getStep(value).getY(), this.getHeight()));
            value++;
        }
    }

    public void setCritterPath3(Path path) {
        critterPath3 = path;
        int value = 0;
        while (value < critterPath3.getLength()) {
            critterPathWorld3.appendStep(critterPath3.getStep(value).getX(), critterPath3.getStep(value).getY());
            value++;
        }
        value = 0;
        while (value < critterPathWorld3.getLength()) {
            critterPathWorld3.getStep(value).setX(Utils.convertXGridToWorld(critterPathWorld3.getStep(value).getX(), this.getWidth()));
            critterPathWorld3.getStep(value).setY(Utils.convertYGridToWorld(critterPathWorld3.getStep(value).getY(), this.getHeight()));
            value++;
        }
    }

    public void setCritterPath4(Path path) {
        critterPath4 = path;
        int value = 0;
        while (value < critterPath4.getLength()) {
            critterPathWorld4.appendStep(critterPath4.getStep(value).getX(), critterPath4.getStep(value).getY());
            value++;
        }
        value = 0;
        while (value < critterPathWorld4.getLength()) {
            critterPathWorld4.getStep(value).setX(Utils.convertXGridToWorld(critterPathWorld4.getStep(value).getX(), this.getWidth()));
            critterPathWorld4.getStep(value).setY(Utils.convertYGridToWorld(critterPathWorld4.getStep(value).getY(), this.getHeight()));
            value++;
        }
    }


    public void calcStartPositionGrid() {
        startGridPosition = Utils.convertXWorldToGrid(getX(), getWidth());
        endGridPosition = Utils.convertYWorldToGrid(getY(), getHeight());
    }

    public int getStartGridPositionX() {
        return startGridPosition;
    }

    public int getStartGridPositionY() {
        return endGridPosition;
    }

    public int getGridPositionX() {
        return gridPositionX;
    }

    public int getGridPositionY() {
        return gridPositionY;
    }

    public Path getCritterWorldPath() {
        int minPath = Math.min(critterPathWorld.getLength(), critterPathWorld2.getLength());

        minPath = Math.min(critterPathWorld3.getLength(), minPath);
        minPath = Math.min(critterPathWorld4.getLength(), minPath);

        if (critterPathWorld.getLength() == minPath)
            return critterPathWorld;
        if (critterPathWorld2.getLength() == minPath)
            return critterPathWorld2;
        if (critterPathWorld3.getLength() == minPath)
            return critterPathWorld3;
        if (critterPathWorld4.getLength() == minPath)
            return critterPathWorld4;

        return critterPathWorld;
    }

    public Path getCritterWorldPath1() {
        return critterPathWorld;
    }

    public Path getCritterWorldPath2() {
        return critterPathWorld2;
    }

    public Path getCritterWorldPath3() {
        return critterPathWorld3;
    }

    public Paint getEnergyBarPaint() {
        return energyBarPaint;
    }

    public Paint getEnergyBarPaintAux() {
        Paint AuxPaint = new Paint();
        AuxPaint.setColor(Color.RED);
        AuxPaint.setAlpha(170);
        return AuxPaint;
    }

    public void draw(Canvas c, int offSetY) {
        c.save();
        c.rotate(getRotationAngle(), getXCenter(), getYCenter() - offSetY);
        getGraphic().setBounds((int) getX(), (int) getY() - offSetY, (int) getX() + getWidth(), (int) getY() + getHeight() - offSetY);
        getGraphic().draw(c);
        c.restore();
        energyBarRect.left = (int) getX();
        energyBarRect.top = (int) getY() - offSetY;
        energyBarRect.right = (int) (getX() + (int) ((getWidth() * lives) / start_live));
        energyBarRect.bottom = (int) (getY() - offSetY - enerbyBarSize);

        if (lives <= 25)
            energyBarPaint.setColor(Color.rgb(214, 0, 48));
        c.drawRect(energyBarRect, getEnergyBarPaint());
    }
}