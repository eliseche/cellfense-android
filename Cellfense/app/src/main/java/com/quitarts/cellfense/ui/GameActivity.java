package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.game.GameSurfaceView;
import com.quitarts.cellfense.game.sound.SoundManager;

/**
 * Main screen used for running the game
 * Here it'll run the SurfaceView (GameSurfaceView)
 */
public class GameActivity extends Activity {
    private GameSurfaceView gameSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        SoundManager.getInstance();

        Bundle extras = getIntent().getExtras();
        int level = extras.getInt("level");

        init(level);
    }

    private void init(int level) {
        gameSurfaceView = new GameSurfaceView(this, level);
        setContentView(gameSurfaceView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            gameSurfaceView.pauseGame();

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Post score via Intent
     *
     * @param subject
     * @param text
     */
    public void postScore(String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.post_score)));
        gameSurfaceView.destroyGame();
    }

    //region Dialogs
    public void showExitDialog() {
        Typeface font1 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");

        final Dialog dialog = new Dialog(this);
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
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSurfaceView.destroyGame();
                dialog.dismiss();
                finish();
            }
        });

        negative.setTypeface(font1);
        negative.setText("No");
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSurfaceView.resumeGame();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showPlayAgainDialog() {
        final Activity context = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Typeface font1 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");

                final Dialog dialog = new Dialog(context);
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
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameSurfaceView.restartGame();
                        dialog.dismiss();
                    }
                });

                negative.setTypeface(font1);
                negative.setText("No");
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameSurfaceView.destroyGame();
                        dialog.dismiss();
                        finish();
                    }
                });

                dialog.show();
            }
        });
    }

    public void showLevelWinDialog() {
        final Activity context = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Typeface font1 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(), "fonts/Discognate.ttf");

                final Dialog dialog = new Dialog(context);
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
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameSurfaceView.restartGame();
                        dialog.dismiss();
                    }
                });

                negative.setTypeface(font1);
                negative.setText("No");
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gameSurfaceView.destroyGame();
                        dialog.dismiss();
                        finish();
                    }
                });

                neutral.setTypeface(font1);
                neutral.setText("Post");
                neutral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "Available soon", Toast.LENGTH_LONG).show();
                    }
                });

                dialog.show();
            }
        });
    }
    //endregion
}
