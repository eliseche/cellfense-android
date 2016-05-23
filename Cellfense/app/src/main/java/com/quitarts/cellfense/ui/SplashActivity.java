package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.quitarts.cellfense.R;

/**
 * Show splash screen for {splashTime} time or cancel on user touch
 */
public class SplashActivity extends Activity {
    private boolean active = true;
    private int splashTime = 2000; // 2 seconds

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        init();
    }

    private void init() {
        Thread threadSplash = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (active && (waited < splashTime)) {
                        sleep(100);
                        if (active)
                            waited += 100;
                    }
                } catch (Exception e) {
                } finally {
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }
        };
        threadSplash.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            active = false;

        return true;
    }
}