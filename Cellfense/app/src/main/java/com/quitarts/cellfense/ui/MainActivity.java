package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;

public class MainActivity extends Activity implements OnClickListener {
    private SharedPreferences sharedPreferences;
    private Button buttonUnlockedArt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();
        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_button_newgame:
                startActivity(new Intent(MainActivity.this, LevelActivity.class));
                break;
            case R.id.main_button_unlocked:
                startActivity(new Intent(MainActivity.this, ArtActivity.class));
                break;
            case R.id.main_button_options:
                startActivity(new Intent(MainActivity.this, OptionsPreferenceActivity.class));
                break;
            case R.id.main_button_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkUnlockedArt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        // Set ApplicationContext
        ContextContainer.setApplicationContext(getApplicationContext());
        // Set canvas
        Display display = getWindowManager().getDefaultDisplay();
        Utils.setCanvasSize(display.getWidth(), display.getHeight());
        // Get SharedPreferences
        sharedPreferences = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
    }

    private void initViews() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/apexnew_medium.ttf");

        Button buttonNewGame = (Button) findViewById(R.id.main_button_newgame);
        buttonNewGame.setTypeface(typeface);
        buttonNewGame.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_menu));
        buttonNewGame.setOnClickListener(this);

        Button buttonOptions = (Button) findViewById(R.id.main_button_options);
        buttonOptions.setTypeface(typeface);
        buttonOptions.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_menu));
        buttonOptions.setOnClickListener(this);

        Button buttonAbout = (Button) findViewById(R.id.main_button_about);
        buttonAbout.setTypeface(typeface);
        buttonAbout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_menu));
        buttonAbout.setOnClickListener(this);

        buttonUnlockedArt = (Button) findViewById(R.id.main_button_unlocked);
        buttonUnlockedArt.setTypeface(typeface);
        buttonUnlockedArt.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_menu));
        buttonUnlockedArt.setOnClickListener(this);
    }

    /**
     * Check if the button (Unlocked Art) should be displayed.
     */
    private void checkUnlockedArt() {
        // TODO: UNLOCKED_ART should by int value instead of string.
        String result = sharedPreferences.getString(Utils.UNLOCKED_ART, "0");

        if (!result.equals("0"))
            buttonUnlockedArt.setVisibility(View.VISIBLE);
        else
            buttonUnlockedArt.setVisibility(View.GONE);
    }
}