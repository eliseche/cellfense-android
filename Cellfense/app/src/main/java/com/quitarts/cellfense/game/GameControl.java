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

public class GameControl {
    private GameSurfaceView gameSurfaceView;
    private SurfaceHolder surfaceHolder;
    private Hud hud;
    private GameWorld gameWorld;
    private boolean playing = true;
    private boolean paused = false;
    private GameState gameState = GameState.SCREEN1;
    private EnemyState enemyState = EnemyState.UNAVAILABLE;

    private enum GameState {
        SCREEN1, SCREEN2
    }

    private enum EnemyState {
        UNAVAILABLE, FROZEN, MOVING
    }

    public GameControl(GameSurfaceView gameSurfaceView, SurfaceHolder surfaceHolder, int startLevel) {
        this.gameSurfaceView = gameSurfaceView;
        this.surfaceHolder = surfaceHolder;
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
            enemyState = EnemyState.FROZEN;
        }

        gameWorld.update(dt);
        hud.update(dt);
    }

    // Draw Game
    public void draw(Canvas canvas) {
        if (canvas != null) {
            gameWorld.drawWorld(canvas);
            hud.draw(canvas);
        }
    }

    // region Events
    public boolean eventActionDown(MotionEvent ev) {
        return true;
    }

    public boolean eventActionMove(MotionEvent ev) {
        return true;
    }

    public boolean eventActionUp(MotionEvent ev) {
        // Click hud button arrow
        if (enemyState == EnemyState.FROZEN && hud.buttonHudArrowClicked((int) ev.getX(), (int) ev.getY())) {
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

    private void initialize() {
        SharedPreferences sharedPreferences = ContextContainer.getContext().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        Typeface tf = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");
        Typeface tf2 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/apexnew_medium.ttf");
    }
}
