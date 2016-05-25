package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.GameSurfaceView;
import com.quitarts.cellfense.LevelDataSet;
import com.quitarts.cellfense.LevelXmlHandler;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class LevelActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private LevelAdapter levelAdapter;
    private GameSurfaceView gameSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_level_selector);

        init();

        ContextContainer.setApplicationContext(this);
        Utils.setLevelUnlockedValues();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // TODO: Launch surfaceview in new Activity, easier to handle instead of call methods via handlers
        if (levelAdapter.getLevelStates().get(i)) {
            gameSurfaceView = new GameSurfaceView(LevelActivity.this, this, i);
            setContentView(gameSurfaceView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO: Not checked in refactor
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (gameSurfaceView != null && !gameSurfaceView.getGameControl().isGamePaused()) {
                gameSurfaceView.getGameControl().pause();
                gameSurfaceView.showExitConfirmDialog(LevelActivity.this);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        loadAndParseLevels();
        LinkedHashMap<Integer, ArrayList<String>> levels = LevelDataSet.getLevels();
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_WORLD_READABLE);

        LinkedHashMap<Integer, String> levelScore = new LinkedHashMap<>();
        for (Integer level : levels.keySet())
            levelScore.put(level, sharedPreferences.getString(level.toString(), ""));

        levelAdapter = new LevelAdapter(this, levelScore);
        gridView = (GridView) findViewById(R.id.level_selector_gridview);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(levelAdapter);
    }

    private void loadAndParseLevels() {
        try {
            LevelDataSet.reset();
            // Own class to deserealize using SAX parser
            LevelXmlHandler levelXmlHandler = new LevelXmlHandler();

            // Using SAX parser to parse levels xml
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(levelXmlHandler);

            // Get levels xml from raw resources
            InputStream inputStreamLevels = getResources().openRawResource(R.raw.levels);
            xmlReader.parse(new InputSource(inputStreamLevels));
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
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
        gameSurfaceView.destroyGameThread();
    }
}