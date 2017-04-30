package com.quitarts.cellfense.game.object;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.GameRules;
import com.quitarts.cellfense.game.object.base.TileAnimation;

public class Tower extends TileAnimation {
    private TowerType type;
    private int xGrid;
    private int yGrid;
    private BitmapDrawable turretBase;
    private float shootingRange;
    private Paint shootingRangePaint;

    public enum TowerType {
        TURRET_CAPACITOR,
        TURRET_TANK,
        TURRET_BOMB
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
        xGrid = Utils.convertYWorldToGrid(y);
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

    private void initialize() {
        shootingRange = GameRules.getTowerShootingRange(type);

        shootingRangePaint = new Paint();
        shootingRangePaint.setAlpha(255);
        shootingRangePaint.setAntiAlias(false);
        shootingRangePaint.setStyle(Paint.Style.STROKE);
        shootingRangePaint.setColor(Color.rgb(0, 120, 0));
    }
}
