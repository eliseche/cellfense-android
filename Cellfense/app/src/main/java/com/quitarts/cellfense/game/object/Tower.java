package com.quitarts.cellfense.game.object;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.GameRules;
import com.quitarts.cellfense.game.object.base.TileAnimation;

import java.util.List;

public class Tower extends TileAnimation {
    private TowerType type;
    private int xGrid;
    private int yGrid;
    private BitmapDrawable turretBase;
    private float shootingRange;
    private Paint shootingRangePaint;
    private int shootingTime;
    private int accumShootingTime;
    private Critter victim;

    public enum TowerType {
        TURRET_CAPACITOR,
        TURRET_TANK,
        TURRET_BOMB
    }

    public Critter getVictim() {
        return victim;
    }

    public void setVictim(Critter victim) {
        this.victim = victim;
    }

    public Tower(FactoryDrawable.DrawableType drawableType, int rows, int columns, int frameSkipDelay, boolean repeatAnimation) {
        super(drawableType, rows, columns, frameSkipDelay, repeatAnimation);

        if (drawableType == FactoryDrawable.DrawableType.GUN_TURRET_CAPACITOR_SPRITE)
            type = TowerType.TURRET_CAPACITOR;
        else if (drawableType == FactoryDrawable.DrawableType.GUN_TURRET_TANK_SPRITE) {
            type = TowerType.TURRET_TANK;
            turretBase = FactoryDrawable.createDrawable(FactoryDrawable.DrawableType.GUN_TURRET_TANK_BASE);
        } else if (drawableType == FactoryDrawable.DrawableType.GUN_TURRET_BOMB_SPRITE)
            type = TowerType.TURRET_BOMB;

        initialize();
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        xGrid = Utils.convertXWorldToGrid(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        yGrid = Utils.convertYWorldToGrid(y);
    }

    public int getXFix() {
        return ((int) getX() / getWidth()) * getWidth();
    }

    public int getYFix() {
        return ((int) getY() / getHeight()) * getHeight();
    }

    public int getXGrid() {
        return xGrid;
    }

    public int getYGrid() {
        return yGrid;
    }

    public TowerType getType() {
        return type;
    }

    public BitmapDrawable getTurretBase() {
        return turretBase;
    }

    public float getShootingRange() {
        return shootingRange;
    }

    public Paint getShootingRangePaint() {
        return shootingRangePaint;
    }

    public void aim(Critter critter, int offsetY) {
        double dx = critter.getXCenter() - getXCenter();
        double dy = critter.getYCenter() - offsetY - (Utils.getCanvasHeight() + getYCenter() - offsetY);
        int angle = (int) Math.toDegrees(Math.atan2(dx, dy));

        if (angle < 0)
            angle = 180 + Math.abs(angle);
        else if (angle >= 0)
            angle = 180 - angle;

        setRotationAngle(angle);
    }

    @Override
    public void updateTile(int dt) {
        super.updateTile(dt);

        accumShootingTime += dt;
    }

    public boolean isEnemyOnRange() {
        return getCritterDistance(this.getVictim()) <= this.getShootingRange();
    }

    public void justShoot() {
        accumShootingTime = 0;
    }

    public boolean mustShoot() {
        return accumShootingTime >= shootingTime;
    }

    public void findNearestCritter(List<Critter> critters) {
        Critter nearestCritter = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Critter critter : critters) {
            double distance = getCritterDistance(critter);

            if (distance <= nearestDistance && distance <= this.getShootingRange() ||
                    (distance <= nearestDistance && Math.abs(distance - this.getShootingRange()) < 0.001)) {
                nearestDistance = distance;
                nearestCritter = critter;
            }
        }

        if (nearestCritter != null)
            this.setVictim(nearestCritter);
    }

    private double getCritterDistance(Critter critter) {
        double dx = Math.abs(this.getXCenter() - critter.getXCenter());
        double dy = Math.abs((Utils.getCanvasHeight() + this.getYCenter()) - critter.getYCenter());
        double ndx, ndy;

        if (dx < dy) {
            double prop = dx / dy;
            ndy = Utils.getCellHeight() / 2;
            ndx = ndy * prop;
        } else {
            double prop = dy / dx;
            ndx = Utils.getCellHeight() / 2;
            ndy = ndx * prop;
        }

        return Math.sqrt(dx * dx + dy * dy) - Math.sqrt(ndx * ndx + ndy * ndy);
    }

    private void initialize() {
        shootingTime = GameRules.getTowerShootingTime(type);
        shootingRange = GameRules.getTowerShootingRange(type);

        shootingRangePaint = new Paint();
        shootingRangePaint.setAlpha(255);
        shootingRangePaint.setAntiAlias(false);
        shootingRangePaint.setStyle(Paint.Style.STROKE);
        shootingRangePaint.setColor(Color.rgb(0, 120, 0));
    }
}
