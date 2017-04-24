package com.quitarts.cellfense.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Bullet;
import com.quitarts.cellfense.game.object.Lta;
import com.quitarts.cellfense.game.object.Tower;

import java.util.ArrayList;

public class GameWorld {
    private int width;
    private int height;
    private int heightVisible;
    private GameControl gameControl;
    private Bitmap background;
    private int offsetY;
    private int deltaPositionY;
    private Lta lta;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();

    public GameWorld(int width, int height, GameControl gameControl) {
        this.width = width;
        this.height = height * 2;
        this.heightVisible = height;
        this.gameControl = gameControl;
        background = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.screen_background),
                this.width, this.height, false);
        // Lta
        lta = new Lta(FactoryDrawable.DrawableType.GUN_LTA_POWER_SPRITE, 1, 15, 100);
        lta.start();
    }

    // region Update world
    public void update(int dt) {
        processOffsetY();
        processLta(dt);
        proccesBullets(dt);
    }

    // Update offsetY value to be used to slide background
    private void processOffsetY() {
        offsetY += deltaPositionY;
        if (offsetY < 0)
            offsetY = 0;
        else if (offsetY > height / 2)
            offsetY = height / 2;
    }

    private void processLta(int dt) {
        lta.updateTile(dt);
        lta.setX(Utils.getCanvasWidth() / 2 - (lta.getWidth() / 2));
        lta.setY(height - (lta.getHeight() + lta.getHeight() / 2) - offsetY);
    }

    private void proccesBullets(int dt) {
        ArrayList<Bullet> bulletsTemp = (ArrayList<Bullet>) bullets.clone();
        if (bulletsTemp.size() > 0) {
            for (Bullet bulletTemp : bulletsTemp) {
                // Clean bullet out of the screen
                if (bulletTemp.getXCenter() < 0 || bulletTemp.getXCenter() > Utils.getCanvasWidth() ||
                        bulletTemp.getYCenter() < 0 || bulletTemp.getYCenter() > Utils.getCanvasHeight())
                    bullets.remove(bulletTemp);
            }

            synchronized (bullets) {
                for (Bullet bullet : bullets) {
                    bullet.advance(dt);
                    bullet.updateTile(dt);
                }
            }
        }
    }
    // endregion

    // region Draw world
    public void drawWorld(Canvas canvas) {
        drawBackground(canvas);
        drawLta(canvas);
        drawBullets(canvas);
        drawTowers(canvas);
    }

    // Draw backgorund image, slide it based on offsetY
    private void drawBackground(Canvas canvas) {
        canvas.drawBitmap(background, 0, -offsetY, null);
    }

    private void drawLta(Canvas canvas) {
        lta.getGraphic().draw(canvas);
    }

    private void drawBullets(Canvas canvas) {
        synchronized (bullets) {
            for (Bullet bullet : bullets) {
                canvas.save();
                canvas.rotate(bullet.getRotationAngle(), bullet.getXCenter(), bullet.getYCenter() + heightVisible - offsetY);
                bullet.getGraphic().setBounds((int) bullet.getX(), (int) bullet.getY() + heightVisible - offsetY, (int) bullet.getX() + bullet.getWidth(), (int) bullet.getY() + heightVisible + bullet.getHeight() - offsetY);
                bullet.getGraphic().draw(canvas);
                canvas.restore();
            }
        }
    }

    private void drawTowers(Canvas canvas) {
        synchronized (towers) {
            for (Tower tower : towers) {
                tower.getGraphic().setBounds((int) tower.getX(), (int) tower.getY() + heightVisible - offsetY, (int) tower.getX() + tower.getWidth(), (int) tower.getY() + heightVisible + tower.getHeight() - offsetY);

                if (tower.getType() == Tower.TowerType.TURRET_TANK) {
                    BitmapDrawable turretTankBase = tower.getTurretBase();
                    turretTankBase.setBounds(tower.getGraphic().getBounds());
                    turretTankBase.draw(canvas);
                }

                canvas.save();
                tower.getGraphic().draw(canvas);
                canvas.restore();
            }
        }
    }
    // endregion

    public void slideToTopScreen() {
        deltaPositionY = -45;
    }

    public void slideToBottomScreen() {
        deltaPositionY = 45;
    }

    public boolean isLtaTouch(int x, int y) {
        return lta.isClicked(x, y);
    }

    public void addBulletToWorld(Bullet bullet) {
        synchronized (bullets) {
            bullets.add(bullet);
        }
    }

    public void addTowerToWorld(Tower tower) {
        synchronized (towers) {
            towers.add(tower);
        }
    }

    public void drawAddingTower(Canvas canvas, Tower tower) {
        if (tower != null) {
            synchronized (tower) {
                canvas.drawCircle(tower.getXCenter(), tower.getYCenter(), tower.getShootingRange(), tower.getShootingRangePaint());
                if (tower.getType() == Tower.TowerType.TURRET_TANK) {
                    BitmapDrawable turretTankBase = tower.getTurretBase();
                    turretTankBase.draw(canvas);
                }
                tower.getGraphic().draw(canvas);
            }
        }
    }

    public boolean worldHaveTowers() {
        return towers.size() > 0;
    }
}