package com.quitarts.cellfense.game;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.torefactor.GameRules;
import com.quitarts.cellfense.torefactor.SoundManager;
import com.quitarts.cellfense.torefactor.Tower;

public class GameControl {
    private GameSurfaceView gameSurfaceView;
    private SurfaceHolder surfaceHolder;
    private GameWorld gameWorld;
    private Hud hud;
    private SharedPreferences sharedPreferences;
    private boolean playing = true;
    private boolean isGamePaused;
    private int ticks = 0;
    private long accumDt = 0;
    private GameState gameState = GameState.SCREEN1;
    private EnemyState enemyState = EnemyState.FROZEN;

    private enum GameState {
        SCREEN1, SCREEN2
    }

    private enum EnemyState {
        UNAVAILABLE, FROZEN, MOVING
    }

    public GameControl(GameSurfaceView gameSurfaceView, SurfaceHolder surfaceHolder, int startLevel) {
        this.gameSurfaceView = gameSurfaceView;
        this.surfaceHolder = surfaceHolder;

        sharedPreferences = ContextContainer.getContext().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
        Typeface tf = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");
        Typeface tf2 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/apexnew_medium.ttf");
    }

    public void play() {
        int FramePerSecondControlValue = Utils.getFramePerSecondControlValue();
        int dt = 0, fullDt = 0;

        Canvas c = surfaceHolder.lockCanvas(null);
        gameWorld = new GameWorld(c.getWidth(), c.getHeight(), this);
        hud = new Hud(this);
        surfaceHolder.unlockCanvasAndPost(c);
        gameSurfaceView.cancelLoading();

        playing = true;

        while (playing) {
            long timeBeforeDraw = System.currentTimeMillis();
            boolean updateFlag = false;
            if (!isGamePaused) {
                updateFlag = false;
                fullDt += dt;

                //if running slow force update method
                while (fullDt > FramePerSecondControlValue) {
                    update(FramePerSecondControlValue);
                    fullDt -= FramePerSecondControlValue;
                    updateFlag = true;
                }
            } else
                updateFlag = true;

            if (updateFlag) {
                synchronized (surfaceHolder) {
                    c = surfaceHolder.lockCanvas(null);
                    draw(c, dt);
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            } else {
                try {
                    Thread.sleep(FramePerSecondControlValue / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ticks++;
            dt = (int) (System.currentTimeMillis() - timeBeforeDraw);
            accumDt += dt;
        }
    }

    public void update(int dt) {
        gameWorld.update(dt);
    }

    public void draw(Canvas canvas, int dt) {
        if (canvas != null) {
            gameWorld.drawWorld(canvas);
            hud.draw(canvas, dt);
        }
    }

    public boolean eventActionDown(MotionEvent ev) {
        return true;
    }

    public boolean eventActionMove(MotionEvent ev) {
        return true;
    }

    public boolean eventActionUp(MotionEvent ev) {
		/*
		 * Click button slide down to Screen2
		 */
        if(gameState == GameState.SCREEN1 && enemyState == EnemyState.FROZEN && hud.buttonDownClicked((int)ev.getX(), (int)ev.getY())) {
            gameState = GameState.SCREEN2;

            gameWorld.slideToBottomScreen();
        }

        /*
		 * Click button slide up to Screen1
		 */
        else if(gameState == GameState.SCREEN2 && enemyState == EnemyState.FROZEN && hud.buttonDownClicked((int)ev.getX(), (int)ev.getY())) {
            gameState = GameState.SCREEN1;
            gameWorld.slideToTopScreen();

        }

        return true;
    }

    public void pause() {
        isGamePaused = true;
    }

    public void resume(){
        isGamePaused = false;
    }

    public void pauseFull(){
        isGamePaused = true;
    }

    public void resumeFull(){
        isGamePaused = false;
    }

    public boolean isGamePaused(){
        return isGamePaused;
    }

    public void stop(){
        playing = false;
    }
}
