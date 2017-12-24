package com.quitarts.cellfense.game;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quitarts.cellfense.ContextContainer;
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
    private Dialog dialog;

    public GameSurfaceView(Context context, int level) {
        super(context);

        this.gameActivity = (GameActivity) context;
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        this.gameControl = new GameControl(this, surfaceHolder, level);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        showLoading();

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
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        gameControl.pause();
        //if (gameThread != null)
        //SoundManager.getInstance().stopAllMusic();
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
            SoundManager.getInstance().stopAllMusic();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
    }

    public void showLoading() {
        dialog = new Dialog(gameActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading);
        dialog.show();
    }

    public void cancelLoading() {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
    }

    public void showExitDialog() {
        Typeface font1 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");

        final Dialog dialog = new Dialog(gameActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_yes_no);

        TextView description = (TextView) dialog.findViewById(R.id.alert_yes_no_description);
        Button positive = (Button) dialog.findViewById(R.id.alert_yes_no_positive);
        Button negative = (Button) dialog.findViewById(R.id.alert_yes_no_negative);

        description.setTypeface(font1);
        description.setText("Exit Game?");

        positive.setTypeface(font1);
        positive.setText("Yes");
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                destroyGame();
                gameActivity.finish();
                dialog.dismiss();
            }
        });

        negative.setTypeface(font1);
        negative.setText("No");
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeGame();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showPlayAgainDialog() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Typeface font1 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");

                final Dialog dialog = new Dialog(gameActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.alert_yes_no);

                TextView description = (TextView) dialog.findViewById(R.id.alert_yes_no_description);
                Button positive = (Button) dialog.findViewById(R.id.alert_yes_no_positive);
                Button negative = (Button) dialog.findViewById(R.id.alert_yes_no_negative);

                description.setTypeface(font1);
                description.setText("Do you want to play again?");

                positive.setTypeface(font1);
                positive.setText("Yes");
                positive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameControl.reset();
                        gameControl.resume();
                        dialog.dismiss();
                    }
                });

                negative.setTypeface(font1);
                negative.setText("No");
                negative.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        destroyGame();
                        gameActivity.finish();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    public void showLevelWinDialog() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Typeface font1 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");

                final Dialog dialog = new Dialog(gameActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.alert_yes_no_neutral);

                TextView description = (TextView) dialog.findViewById(R.id.alert_yes_no_neutral_description);
                Button positive = (Button) dialog.findViewById(R.id.alert_yes_no_neutral_positive);
                Button negative = (Button) dialog.findViewById(R.id.alert_yes_no_neutral_negative);
                Button neutral = (Button) dialog.findViewById(R.id.alert_yes_no_neutral_neutral);

                description.setTypeface(font1);
                description.setText("LEVEL COMPLETE!\nPlay again?");

                positive.setTypeface(font1);
                positive.setText("Yes");
                positive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameControl.reset();
                        gameControl.resume();
                        dialog.dismiss();
                    }
                });

                negative.setTypeface(font1);
                negative.setText("No");
                negative.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        destroyGame();
                        gameActivity.finish();
                        dialog.dismiss();
                    }
                });

                neutral.setTypeface(font1);
                neutral.setText("Post");
                neutral.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(gameActivity, "Available soon", Toast.LENGTH_LONG).show();
                    }
                });

                dialog.show();
            }
        });
    }
}