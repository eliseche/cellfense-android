package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

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
}
