package com.quitarts.cellfense.game;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Bullet;
import com.quitarts.cellfense.game.object.Critter;
import com.quitarts.cellfense.game.object.Tower;
import com.quitarts.cellfense.game.sound.SoundManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GameControl {
    private long accumDt = 0;
    private GameSurfaceView gameSurfaceView;
    private SurfaceHolder surfaceHolder;
    private Hud hud;
    private GameWorld gameWorld;
    private boolean playing = true;
    private boolean paused = false;
    private GameState gameState = GameState.SCREEN1;
    private EnemyState enemyState = EnemyState.UNAVAILABLE;
    private int actionMoveX;
    private int actionMoveY;
    private long holdingDownStartTime;
    private boolean ltaStartShoot;
    private Config config;
    private Tower addTower;
    private boolean executeLevel = false;
    private boolean movingAddedTower;
    private boolean pathBlock;
    private Paint pathBlockMessagePaint;
    private int pathBlockMessageAccumDt;
    private boolean sellTower;
    private Paint sellTowerMessagePaint;
    private boolean resetResources = true;

    public enum GameState {
        SCREEN1, SCREEN2
    }

    public enum EnemyState {
        UNAVAILABLE, FROZEN, MOVING
    }

    public GameControl(GameSurfaceView gameSurfaceView, SurfaceHolder surfaceHolder, int startLevel) {
        this.gameSurfaceView = gameSurfaceView;
        this.surfaceHolder = surfaceHolder;

        initialize();

        this.config = new Config();
        config.wave = startLevel;
    }

    // Game main loop, update and draw world
    public void play() {
        int fps = Utils.getFramesPerSecond();
        long dt = 0;

        hud = new Hud(this);
        Canvas canvas = surfaceHolder.lockCanvas();
        gameWorld = new GameWorld(canvas.getWidth(), canvas.getHeight(), this);
        surfaceHolder.unlockCanvasAndPost(canvas);

        gameSurfaceView.cancelLoading();

        playing = true;
        while (playing) {
            long timeBeforeDraw = System.currentTimeMillis();
            boolean updateFlag = false;
            // Update game
            if (!isGamePaused()) {
                // If running slow, froce update method
                while (accumDt > fps) {
                    update(fps);
                    accumDt -= fps;
                    updateFlag = true;
                }
            } else {
                updateFlag = true;
                accumDt = 0;
            }

            // Draw if updateFlag = true, else sleep
            if (updateFlag) {
                synchronized (surfaceHolder) {
                    canvas = surfaceHolder.lockCanvas();
                    draw(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } else {
                try {
                    Thread.sleep(fps / 2);
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.getMessage(), e);
                }
            }

            dt = System.currentTimeMillis() - timeBeforeDraw;
            accumDt += dt;
        }
    }

    // Update Game
    public void update(int dt) {
        if (enemyState == EnemyState.UNAVAILABLE) {
            executeLevel = false;
            createNewHorde();
            enemyState = EnemyState.FROZEN;
        } else if (enemyState == EnemyState.FROZEN) {
            if (executeLevel) {
                enemyState = EnemyState.MOVING;
                gameState = GameState.SCREEN2;
                gameWorld.slideToBottomScreen();
                gameWorld.calculateCrittersPath();
            } else
                gameWorld.resetTowerAngle();

            if (gameState == GameState.SCREEN1)
                hud.switchOffNextWaveButton();
            else if (gameState == GameState.SCREEN2) {
                if (gameWorld.worldHaveTowers())
                    hud.switchOnNextWaveButton();
                else
                    hud.switchOffNextWaveButton();
            }
        } else if (enemyState == EnemyState.MOVING) {
            if (!gameWorld.worldHaveEnemies() && !isGamePaused()) {
                pause();

                if (config.lives > 0) {
                    // Win
                    gameSurfaceView.showLevelWinDialog();
                } else {
                    // Lose
                    gameSurfaceView.showPlayAgainDialog();
                }
            }
        }

        gameWorld.update(dt);
        hud.update(dt);
        updateBlockingMessage(dt);
    }

    // Draw Game
    public void draw(Canvas canvas) {
        if (canvas != null) {
            gameWorld.drawWorld(canvas);
            if (gameState == GameState.SCREEN2 && enemyState == EnemyState.FROZEN) {
                hud.drawBottomHud(canvas);
                gameWorld.drawAddingTower(canvas, addTower);
            }
            hud.drawBaseHud(canvas);
            drawBlockingMessage(canvas);
            drawSellMessage(canvas);
        }
    }

    // region Events
    public boolean eventActionDown(MotionEvent ev) {
        if (enemyState == EnemyState.MOVING) {
            // User is trying to shoot using LTA
            if (gameWorld.isLtaTouch((int) ev.getX(), (int) ev.getY()) &&
                    config.resources >= GameRules.getLTAPrice()) {
                ltaStartShoot = true;
                actionMoveX = (int) ev.getX();
                actionMoveY = (int) ev.getY();
                holdingDownStartTime = System.currentTimeMillis();
            }

            // User activate tower special ability (bomb or crazy tower)
            Tower tower = gameWorld.getTower((int) ev.getX(), (int) ev.getY());
            if (tower != null) {
                if (tower.getType() == Tower.TowerType.TURRET_BOMB && tower.hasCharge()) {
                    tower.detonate();
                    ((Vibrator) ContextContainer.getContext().getSystemService(ContextContainer.getContext().VIBRATOR_SERVICE)).vibrate(300);
                } else if (tower.getType() != Tower.TowerType.TURRET_BOMB && !tower.isCrazy() &&
                        config.resources >= GameRules.getTowerCrazyPrice()) {
                    tower.setCrazy(true);
                    config.resources -= GameRules.getTowerCrazyPrice();
                }
            }
        }

        if (enemyState == EnemyState.FROZEN) {
            // User is picking a tower
            if (hud.hudTowerClick((int) ev.getX(), (int) ev.getY())) {
                addTower.setX(ev.getX());
                addTower.setY(hud.getTopBoundOfHud() - addTower.getHeight());
            }

            // User is moving a tower
            if (gameWorld.isTowerTouch((int) ev.getX(), (int) ev.getY())) {
                Tower tower = gameWorld.getTower((int) ev.getX(), (int) ev.getY());
                gameWorld.removeTower(tower);
                addTower = tower;
                movingAddedTower = true;
            }
        }

        return true;
    }

    public boolean eventActionMove(MotionEvent ev) {
        if (addTower != null) {
            // User is sliding a Tower
            // Set sellTower flag to display sell message if touching HUD
            if (hud.isHudAreaTouch((int) ev.getY())) {
                sellTower = true;

                addTower.setX(ev.getX());
                addTower.setY(hud.getTopBoundOfHud() - addTower.getHeight());
                addTower.setX(addTower.getXFix());
                addTower.setY(addTower.getYFix());
            } else {
                sellTower = false;

                addTower.setX(ev.getX());
                addTower.setY(ev.getY() - addTower.getHeight());
                addTower.setX(addTower.getXFix());
                addTower.setY(addTower.getYFix());
            }

            // Change color if tower is over another placed tower
            if (gameWorld.isEmptyPlace(addTower))
                addTower.getGraphic().mutate().setColorFilter(null);
            else
                addTower.getGraphic().mutate().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.SRC_IN);
        }

        return true;
    }

    public boolean eventActionUp(MotionEvent ev) {
        if (enemyState == EnemyState.FROZEN) {
            // Click hud button arrow
            if (hud.buttonHudArrowClicked((int) ev.getX(), (int) ev.getY())) {
                // Slide to SCREEN2
                if (gameState == GameState.SCREEN1) {
                    gameState = GameState.SCREEN2;
                    gameWorld.slideToBottomScreen();
                }
                // Slide to SCREEN1
                else if (gameState == GameState.SCREEN2) {
                    gameState = GameState.SCREEN1;
                    gameWorld.slideToTopScreen();
                }
            }

            // Release Tower
            if (addTower != null) {
                if (gameWorld.isBlocking(addTower)) {
                    pathBlock = true;
                    SoundManager.getInstance().playSound(SoundManager.Sound.LOCK2);
                } else if (hud.isHudAreaTouch((int) ev.getY()) || !gameWorld.isEmptyPlace(addTower)) {
                    if (movingAddedTower)
                        config.resources += addTower.getPrice();

                    SoundManager.getInstance().playSound(SoundManager.Sound.LOCK2);
                } else if (gameWorld.isEmptyPlace(addTower)) {
                    if (movingAddedTower) {
                        gameWorld.addTower(addTower);
                    } else {
                        if (addTower.getPrice() <= config.resources && gameWorld.getTowersCount() <= config.maxUnits) {
                            gameWorld.addTower(addTower);
                            config.resources -= addTower.getPrice();
                            SoundManager.getInstance().playSound(SoundManager.Sound.LOCK1);
                        } else
                            SoundManager.getInstance().playSound(SoundManager.Sound.LOCK2);
                    }
                }

                movingAddedTower = false;
                sellTower = false;
                synchronized (addTower) {
                    addTower = null;
                }
            }

            // Click button to send next wave
            if (gameState == GameState.SCREEN2 &&
                    hud.nextWaveClicked((int) ev.getX(), (int) ev.getY())) {
                executeLevel = true;
                sendCrittersWave();
                SoundManager.getInstance().stopAllMusic();
                SoundManager.getInstance().playMusic(SoundManager.Music.ACTION, true);
            }
        }

        if (enemyState == EnemyState.MOVING) {
            // Firing LTA
            if (ltaStartShoot)
                shootLTA(ev);
        }

        return true;
    }
    // endregion

    public void resume() {
        paused = false;
    }

    public void pause() {
        paused = true;
    }

    public void stop() {
        playing = false;
    }

    public boolean isGamePaused() {
        return paused;
    }

    private void shootLTA(MotionEvent ev) {
        int dx = (int) ev.getX() - actionMoveX;
        int dy = (int) ev.getY() - actionMoveY;
        if (dx != 0 && dy != 0) {
            float dt = (System.currentTimeMillis() - holdingDownStartTime);
            // Convert dt to seconds
            dt = dt / 1000;
            float velX = dx / dt;
            float velY = dy / dt;
            int angle = (int) Math.toDegrees(Math.atan2(dx, dy));
            if (angle < 0)
                angle = 180 + Math.abs(angle);
            else if (angle >= 0)
                angle = 180 - angle;

            Bullet bullet = new Bullet(FactoryDrawable.DrawableType.GUN_LTA_FIRE_SPRITE, 1, 15, 15);
            bullet.setRotationAngle(angle);
            bullet.setX(ev.getX());
            bullet.setY(ev.getY());
            bullet.setDirection(1, 1);
            bullet.setSpeed(velX, velY);
            bullet.start();
            gameWorld.addBullet((Bullet) bullet.clone());
            config.resources -= GameRules.getLTAPrice();
            SoundManager.getInstance().playSound(SoundManager.Sound.FIREBALL);
        }

        ltaStartShoot = false;
    }

    public int getWave() {
        return config.wave;
    }

    public void addTower(Tower tower) {
        addTower = tower;
    }

    public void sendCrittersWave() {
        executeLevel = true;
    }

    public EnemyState getEnemyState() {
        return enemyState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void createNewHorde() {
        LinkedHashMap<Integer, ArrayList<String>> levels = LevelDataSet.getLevels();
        LinkedHashMap<Integer, Integer> resources = LevelDataSet.getResources();

        if (config.wave <= levels.size()) {
            ArrayList<Critter> critters = new CritterFactory().createPresetLevel(levels.get(config.wave));
            gameWorld.addCritters(critters);
            if (resetResources)
                config.resources = resources.get(config.wave);
            else
                config.resources = resources.get(config.wave) - calculateTowersPrice();
        }
    }

    private void initialize() {
        Typeface font1 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");

        pathBlockMessagePaint = new Paint();
        pathBlockMessagePaint.setTypeface(font1);
        pathBlockMessagePaint.setTextSize(30 * Utils.getScaleFactor());
        pathBlockMessagePaint.setColor(Color.WHITE);
        pathBlockMessagePaint.setAntiAlias(true);

        sellTowerMessagePaint = new Paint();
        sellTowerMessagePaint.setTypeface(font1);
        sellTowerMessagePaint.setTextSize(30 * Utils.getScaleFactor());
        sellTowerMessagePaint.setColor(Color.WHITE);
        sellTowerMessagePaint.setAntiAlias(true);
    }

    public int getResources() {
        return config.resources;
    }

    public void removeLife() {
        this.config.lives--;
    }

    public class Config {
        public int lives = GameRules.getStartLives();
        public int score = 0;
        public int resources = 0;
        public int wave = 0;
        public int maxUnits = GameRules.getMaxTowers();
    }

    public void reset() {
        resetResources = false;
        config.lives = GameRules.getStartLives();
        config.score = 0;
        gameWorld.reset();
        enemyState = EnemyState.UNAVAILABLE;
        gameState = GameState.SCREEN1;
        gameWorld.slideToTopScreen();

        SoundManager.getInstance().stopAllMusic();
        SoundManager.getInstance().playMusic(SoundManager.Music.STRATEGY, true);
    }

    private int calculateTowersPrice() {
        return gameWorld.calculateTowersPrice();
    }

    private void updateBlockingMessage(int dt) {
        if (pathBlock) {
            pathBlockMessageAccumDt += dt;
            if (pathBlockMessageAccumDt >= 1500) {
                pathBlock = false;
                pathBlockMessageAccumDt = 0;
            }
        }
    }

    private void drawBlockingMessage(Canvas canvas) {
        if (pathBlock) {
            String blockingMessage = ContextContainer.getContext().getString(R.string.Block_Message);

            canvas.drawText(blockingMessage,
                    Utils.getCanvasWidth() / 2 - pathBlockMessagePaint.measureText(blockingMessage) / 2,
                    Utils.getCanvasHeight() / 2 - Utils.getCellHeight() - pathBlockMessagePaint.getTextSize(),
                    pathBlockMessagePaint);
        }
    }

    private void drawSellMessage(Canvas canvas) {
        if (sellTower) {
            String sellMessage = ContextContainer.getContext().getString(R.string.Sell_Tower);

            canvas.drawText(sellMessage,
                    Utils.getCanvasWidth() / 2 - sellTowerMessagePaint.measureText(sellMessage) / 2,
                    Utils.getCanvasHeight() / 2 - sellTowerMessagePaint.getTextSize(),
                    sellTowerMessagePaint);
        }
    }
}
