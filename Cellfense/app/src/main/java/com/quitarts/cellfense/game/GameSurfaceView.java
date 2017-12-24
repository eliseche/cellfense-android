package com.quitarts.cellfense.game;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.game.sound.SoundManager;
import com.quitarts.cellfense.ui.GameActivity;

/**
 * SurfaceView, where the game will be drawn
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private GameActivity gameActivity;
    private GameControl gameControl;
    private Thread gameThread;
    private ProgressDialog progressDialog;

    public GameSurfaceView(Context context, int level) {
        super(context);

        this.gameActivity = (GameActivity) context;
        showLoading();
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        this.gameControl = new GameControl(this, surfaceHolder, level);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (gameThread == null) {
            gameThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    SoundManager.getInstance().stopAllMusic();
                    SoundManager.getInstance().playMusic(SoundManager.Music.STRATEGY, true);
                    gameControl.play();
                }
            });
            gameThread.start();
        } else {
            gameControl.resume();
        }

        cancelLoading();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        gameControl.pause();
        if (gameThread != null)
            SoundManager.getInstance().stopAllMusic();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                gameControl.eventActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                gameControl.eventActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                gameControl.eventActionUp(event);
                break;
            default:
                break;
        }

        return true;
    }

    public void resumeGame() {
        gameControl.resume();
    }

    public void restartGame() {
        gameControl.reset();
        gameControl.resume();
    }

    public void pauseGame() {
        if (!gameControl.isGamePaused()) {
            gameControl.pause();
            gameActivity.showExitDialog();
        }
    }

    public void destroyGame() {
        gameControl.pause();
        gameControl.stop();
        try {
            gameThread.join();
            gameThread = null;
            SoundManager.getInstance().cleanup();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
    }

    public void showLevelWinDialog() {
        gameActivity.showLevelWinDialog();
    }

    public void showPlayAgainDialog() {
        gameActivity.showPlayAgainDialog();
    }

    public void showLoading() {
        progressDialog = ProgressDialog.show(gameActivity, "", getResources().getText(R.string.loading_message), true);
    }

    public void cancelLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }
}