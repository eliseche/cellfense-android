package com.quitarts.cellfense.game;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Bullet;
import com.quitarts.cellfense.game.object.Critter;
import com.quitarts.cellfense.game.object.Tower;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GameControl {
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

    public enum GameState {
        SCREEN1, SCREEN2
    }

    public enum EnemyState {
        UNAVAILABLE, FROZEN, MOVING
    }

    public GameControl(GameSurfaceView gameSurfaceView, SurfaceHolder surfaceHolder, int startLevel) {
        this.gameSurfaceView = gameSurfaceView;
        this.surfaceHolder = surfaceHolder;

        this.config = new Config();
        config.wave = startLevel;
    }

    // Game main loop, update and draw world
    public void play() {
        int fps = Utils.getFramesPerSecond();
        long dt = 0;
        long accumDt = 0;

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
            } else
                updateFlag = true;

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
            }

            if (gameState == GameState.SCREEN1)
                hud.switchOffNextWaveButton();
            else if (gameState == GameState.SCREEN2) {
                if (gameWorld.worldHaveTowers())
                    hud.switchOnNextWaveButton();
                else
                    hud.switchOffNextWaveButton();
            }
        }

        gameWorld.update(dt);
        hud.update(dt);
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
        }
    }

    // region Events
    public boolean eventActionDown(MotionEvent ev) {
        if (enemyState == EnemyState.MOVING) {
            // User is trying to shoot using LTA
            if (gameWorld.isLtaTouch((int) ev.getX(), (int) ev.getY())) {
                ltaStartShoot = true;
                actionMoveX = (int) ev.getX();
                actionMoveY = (int) ev.getY();
                holdingDownStartTime = System.currentTimeMillis();
            }
        }

        if (enemyState == EnemyState.FROZEN) {
            // User is picking a tower
            if (hud.hudTowerClick((int) ev.getX(), (int) ev.getY())) {
                addTower.setX(ev.getX());
                addTower.setY(hud.getTopBoundOfHud() - addTower.getHeight());
            }
        }

        return true;
    }

    public boolean eventActionMove(MotionEvent ev) {
        // User is sliding a Tower
        if (addTower != null) {
            addTower.setX(ev.getX());
            addTower.setY(ev.getY() - addTower.getHeight());
            addTower.setX(addTower.getXFix());
            addTower.setY(addTower.getYFix());
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
                gameWorld.addTower((Tower) addTower.clone());

                synchronized (addTower) {
                    addTower = null;
                }
            }

            // Click button to send next wave
            if (gameState == GameState.SCREEN2 &&
                    hud.nextWaveClicked((int) ev.getX(), (int) ev.getY())) {
                executeLevel = true;
                sendCrittersWave();
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
        }
    }

    private void initialize() {
        SharedPreferences sharedPreferences = ContextContainer.getContext().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        Typeface tf = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");
        Typeface tf2 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/apexnew_medium.ttf");
    }

    public class Config {
        public int lives = GameRules.getStartLives();
        public int score = 0;
        public int resources = 0;
        public int wave = 0;
        public int maxUnits = GameRules.getMaxTowers();
    }
}
