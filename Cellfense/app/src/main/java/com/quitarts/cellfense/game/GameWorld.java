package com.quitarts.cellfense.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Bullet;
import com.quitarts.cellfense.game.object.Critter;
import com.quitarts.cellfense.game.object.Lta;
import com.quitarts.cellfense.game.object.Tower;
import com.quitarts.pathfinder.AStarPathFinder;
import com.quitarts.pathfinder.Path;
import com.quitarts.pathfinder.PathFinder;

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
    private ArrayList<Critter> critters = new ArrayList<>();
    private GameMap gameMap;

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
        gameMap = new GameMap(Utils.GAMEMAP_WIDTH, Utils.GAMEMAP_HEIGHT);
    }

    // region Update world
    public void update(int dt) {
        processOffsetY();
        processLta(dt);
        processBullets(dt);
        processTowers(dt);
        processCritters(dt);
    }

    // Update offsetY value to be used to slide background
    private void processOffsetY() {
        offsetY += deltaPositionY;
        if (offsetY < 0)
            offsetY = 0;
        else if (offsetY > height / 2)
            offsetY = height / 2;

        Utils.setOffsetY(offsetY);
    }

    private void processLta(int dt) {
        lta.updateTile(dt);
        lta.setX(Utils.getCanvasWidth() / 2 - (lta.getWidth() / 2));
        lta.setY(height - (lta.getHeight() + lta.getHeight() / 2) - offsetY);
    }

    private void processBullets(int dt) {
        ArrayList<Bullet> bulletsTemp = (ArrayList<Bullet>) bullets.clone();
        if (bulletsTemp.size() > 0) {
            for (Bullet bulletTemp : bulletsTemp) {
                // Clean bullet out of the screen
                if (bulletTemp.getXCenter() < 0 || bulletTemp.getXCenter() > Utils.getCanvasWidth() ||
                        bulletTemp.getYCenter() < 0 || bulletTemp.getYCenter() > Utils.getCanvasHeight())
                    bullets.remove(bulletTemp);

                // Clean bullet that hit a critter
                for (Critter critter : critters) {
                    if (critter.isHit(bulletTemp)) {
                        critter.getSlugish();
                        bullets.remove(bulletTemp);
                    }
                }
            }

            synchronized (bullets) {
                for (Bullet bullet : bullets) {
                    bullet.advance(dt);
                    bullet.updateTile(dt);
                }
            }
        }
    }

    private void processTowers(int dt) {
        synchronized (towers) {
            for (Tower tower : towers) {
                if (tower.getVictim() == null || !tower.isEnemyOnRange() || tower.getVictim().getLives() <= 0) {
                    tower.findNearestCritter(critters);
                }

                if (tower.getVictim() != null) {
                    float damage = GameRules.getDamageEnemy(tower, tower.getVictim());
                    tower.aim(tower.getVictim(), offsetY);
                    if (tower.mustShoot()) {
                        tower.start();
                        tower.getVictim().hit(damage);
                        tower.justShoot();

                        if (tower.getVictim().getLives() <= 0) {
                            critters.remove(tower.getVictim());
                            tower.setVictim(null);
                        }
                    }
                }

                // override tower method
                tower.updateTile(dt);
            }
        }
    }

    private void processCritters(int dt) {
        if (gameControl.getEnemyState() == GameControl.EnemyState.MOVING) {
            synchronized (critters) {
                for (Critter critter : critters) {
                    critter.start();
                    critter.advance(dt);
                    critter.updateTile(dt);

                    if (critter.getLives() <= 0)
                        critters.remove(critter);

                    if (critter.getY() > height) {
                        critters.remove(critter);
                        gameControl.removeLife();
                    }
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
        drawCritters(canvas);
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
                canvas.rotate(tower.getRotationAngle(), tower.getXCenter(), heightVisible + tower.getYCenter() - offsetY);
                tower.getGraphic().draw(canvas);
                canvas.restore();
            }
        }
    }

    private void drawCritters(Canvas canvas) {
        synchronized (critters) {
            for (Critter critter : critters) {
                canvas.save();
                canvas.rotate(critter.getRotationAngle(), critter.getXCenter(), critter.getYCenter() - offsetY);
                critter.getGraphic().setBounds((int) critter.getX(), (int) critter.getY() - offsetY, (int) critter.getX() + critter.getWidth(), (int) critter.getY() + critter.getHeight() - offsetY);
                critter.getGraphic().draw(canvas);
                canvas.restore();

                // Draw energyBar
                Rect energyBarRect = new Rect();
                Paint energyBarPaint = new Paint();
                energyBarRect.left = (int) critter.getX();
                energyBarRect.top = (int) critter.getY() - offsetY;
                energyBarRect.right = (int) (critter.getX() + critter.getWidth() * critter.getLives() / 100);
                energyBarRect.bottom = (int) (critter.getY() - offsetY - (Utils.getCellHeight() * 5 / 100));

                if (critter.getLives() <= 25)
                    energyBarPaint.setColor(Color.rgb(214, 0, 48));
                else
                    energyBarPaint.setColor(Color.rgb(43, 180, 9));


                canvas.drawRect(energyBarRect, energyBarPaint);
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

    public void drawAddingTower(Canvas canvas, Tower tower) {
        if (tower != null) {
            synchronized (tower) {
                canvas.drawCircle(tower.getXCenter(), tower.getYCenter(), tower.getShootingRange(), tower.getShootingRangePaint());
                if (tower.getType() == Tower.TowerType.TURRET_TANK) {
                    BitmapDrawable turretTankBase = tower.getTurretBase();
                    turretTankBase.setBounds(tower.getGraphic().getBounds());
                    turretTankBase.draw(canvas);
                }
                tower.getGraphic().draw(canvas);
            }
        }
    }

    public boolean worldHaveTowers() {
        return towers.size() > 0;
    }

    public void addTower(Tower tower) {
        synchronized (towers) {
            towers.add(tower);
            gameMap.setUnit(tower.getXGrid(), tower.getYGrid() + 1, 1);
        }
    }

    public void removeTower(Tower tower) {
        synchronized (towers) {
            towers.remove(tower);
            gameMap.setUnit(tower.getXGrid(), tower.getYGrid() + 1, 0);
        }
    }

    public void addCritters(ArrayList<Critter> critters) {
        synchronized (critters) {
            this.critters = critters;
        }
    }

    public void addBullet(Bullet bullet) {
        synchronized (bullets) {
            bullets.add(bullet);
        }
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void calculateCrittersPath() {
        synchronized (critters) {
            for (Critter critter : critters)
                critter.setCrittersPath(gameMap);
        }
    }

    public boolean isTowerTouch(int x, int y) {
        Tower tower = getTower(x, y);
        if (tower != null)
            return true;

        return false;
    }

    public boolean isEmptyPlace(Tower tower) {
        for (Tower towerPlaced : towers) {
            if (tower.getGraphic().getBounds().intersect(towerPlaced.getGraphic().getBounds()))
                return false;
        }

        return true;
    }

    public boolean isBlocking(Tower tower) {
        boolean isBlocking = false;
        int state = gameMap.getUnit(tower.getXGrid(), tower.getYGrid() + 1);

        gameMap.setUnit(tower.getXGrid(), tower.getYGrid() + 1, 1);
        synchronized (critters) {
            for (Critter critter : critters) {
                if (isBlocking)
                    break;

                PathFinder finder = new AStarPathFinder(gameMap, 500, false);
                for (int position = 0; position < 8; position++) {
                    Path path = finder.findPath(new UnitMover(0), Utils.convertXWorldToGrid(critter.getX()), 0, position, Utils.GAMEMAP_HEIGHT - 1);
                    if (path == null) {
                        isBlocking = true;
                        break;
                    }
                }
            }
        }
        gameMap.setUnit(tower.getXGrid(), tower.getYGrid() + 1, state);

        return isBlocking;
    }

    public Tower getTower(int x, int y) {
        synchronized (towers) {
            for (Tower tower : towers) {
                if (tower.getGraphic().getBounds().contains(x, y))
                    return tower;
            }
        }

        return null;
    }

    public int getTowersCount() {
        return towers.size();
    }
}