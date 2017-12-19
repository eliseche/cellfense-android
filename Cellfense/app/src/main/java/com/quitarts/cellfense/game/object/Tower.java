package com.quitarts.cellfense.game.object;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.GameRules;
import com.quitarts.cellfense.game.object.base.TileAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tower extends TileAnimation {
    private TowerType type;
    private BitmapDrawable turretBase;
    private float xOriginal;
    private float yOriginal;
    private int xGrid;
    private int yGrid;
    private Critter victim;
    private int price;
    // Shooting vars
    private int shootingTime;
    private int accumShootingTime;
    private float shootingRange;
    private Paint shootingRangePaint;
    // Crazy vars
    private boolean isCrazy;
    private int crazyTime = 2000;
    private int accumCrazyTime;
    // Bomb vars
    private boolean isDetonated;
    private boolean isExplosionInProgress;
    private int numberOfExplosions = 1;
    private float explosionRange;
    private Paint explosionRangePaint;
    private MaskFilter bombBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

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
        xOriginal = x;
        xGrid = Utils.convertXWorldToGrid(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        yOriginal = y;
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

        if (isExplosionInProgress)
            processExplosion(dt);

        if (isCrazy)
            processCrazy(dt);
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

    public List<Critter> findNearestCritters(List<Critter> critters) {
        List<Critter> nearestCritters = new ArrayList<>();
        double nearestDistance = getShootingRange();

        for (Critter critter : critters) {
            double distance = getCritterDistance(critter);

            if (distance <= nearestDistance)
                nearestCritters.add(critter);
        }

        return nearestCritters;
    }

    public int getPrice() {
        return price;
    }

    public boolean isCrazy() {
        return isCrazy;
    }

    public void setCrazy(boolean crazy) {
        isCrazy = crazy;
    }

    public boolean isDetonated() {
        return isDetonated;
    }

    public void setDetonated(boolean detonated) {
        isDetonated = detonated;
    }

    public boolean hasCharge() {
        if (numberOfExplosions > 0)
            return true;

        return false;
    }

    public void detonate() {
        isDetonated = true;
        isExplosionInProgress = true;
        numberOfExplosions--;
    }

    public float getExplosionRange() {
        return explosionRange;
    }

    public Paint getExplosionRangePaint() {
        return explosionRangePaint;
    }

    public boolean isExplosionInProgress() {
        return isExplosionInProgress;
    }

    public void resetBombState() {
        isDetonated = false;
        isExplosionInProgress = false;
        numberOfExplosions = 1;

        initialize();

        setTileAnimation(FactoryDrawable.DrawableType.GUN_TURRET_BOMB_SPRITE, 1, 8, 150, true);
        start();
    }

    public double getCritterDistance(Critter critter) {
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
        price = GameRules.getTowerPrice(type);
        shootingTime = GameRules.getTowerShootingTime(type);
        shootingRange = GameRules.getTowerShootingRange(type);
        explosionRange = shootingRange;

        shootingRangePaint = new Paint();
        shootingRangePaint.setAlpha(255);
        shootingRangePaint.setAntiAlias(false);
        shootingRangePaint.setStyle(Paint.Style.STROKE);
        shootingRangePaint.setColor(Color.rgb(0, 120, 0));

        explosionRangePaint = new Paint();
        explosionRangePaint.setAlpha(255);
        explosionRangePaint.setAntiAlias(false);
        explosionRangePaint.setStyle(Paint.Style.STROKE);
        explosionRangePaint.setColor(Color.rgb(0, 0, 60));
        explosionRangePaint.setMaskFilter(bombBlur);
        explosionRangePaint.setStrokeWidth(Utils.getCellWidth());
    }

    private void processExplosion(int dt) {
        if (explosionRangePaint.getAlpha() > 0) {
            float alphaValue = explosionRangePaint.getAlpha() - shootingRangePaint.getAlpha() * 5 / 100;
            float rangeValue = explosionRange - shootingRange * 5 / 100;
            if (alphaValue < 10) {
                setTileAnimation(FactoryDrawable.DrawableType.GUN_TURRET_BOMB_CRATER, 1, 1, 0, false);
                isExplosionInProgress = false;
            } else {
                explosionRangePaint.setAlpha((int) alphaValue);
                explosionRange = rangeValue;
            }
        }
    }

    private void processCrazy(int dt) {
        accumCrazyTime += dt;
        if (accumCrazyTime <= crazyTime) {
            shootingTime = GameRules.getTowerShootingTime(type) / 3;
            setX(xOriginal + new Random().nextInt(3) - 1);
            setY(yOriginal + new Random().nextInt(3) - 1);
            shootingRangePaint.setColor(Color.rgb(180, 0, 0));
            shootingRangePaint.setStrokeWidth(2);
        } else {
            shootingTime = GameRules.getTowerShootingTime(type);
            setX(xOriginal);
            setY(yOriginal);
            shootingRangePaint.setColor(Color.rgb(0, 120, 0));
            shootingRangePaint.setStrokeWidth(1);
            accumCrazyTime = 0;
            isCrazy = false;
        }
    }
}
