package com.quitarts.cellfense.game;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.quitarts.cellfense.R;
import com.quitarts.cellfense.torefactor.SoundManager;
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
                    SoundManager.loadMusic();
                    SoundManager.loadSounds();
                    SoundManager.playMusic(SoundManager.MusicType.STRATEGY, true, true, true);
                    SoundManager.playMusic(SoundManager.MusicType.ACTION, true, true, true);
                    gameControl.play();
                }
            });
            gameThread.start();
        } else {
            gameControl.resumeFull();
            SoundManager.resumeMusics();

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        gameControl.pauseFull();
        if (gameThread != null)
            SoundManager.pauseMusics();
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

    public void pauseGame() {
        if (!gameControl.isGamePaused()) {
            gameControl.pause();
            showExitDialog();
        }
    }

    public void destroyGame() {
        gameControl.stop();
        try {
            gameThread.join();
            gameThread = null;
            SoundManager.cleanup();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
    }

    public void showLoading() {
        progressDialog = ProgressDialog.show(gameActivity, "", getResources().getText(R.string.loading_message), true);
    }

    public void cancelLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    public boolean showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(gameActivity);
        builder.setCancelable(false);
        builder.setMessage(getResources().getText(R.string.dialog_message_exit));
        builder.setPositiveButton(
                getResources().getText(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        destroyGame();
                        gameActivity.finish();
                    }
                });
        builder.setNegativeButton(
                getResources().getText(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resumeGame();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return true;
    }
}