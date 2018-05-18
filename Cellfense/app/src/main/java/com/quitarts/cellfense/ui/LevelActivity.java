package com.quitarts.cellfense.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.LevelDataSet;
import com.quitarts.cellfense.helpers.ParserLevelXml;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Screen that display list of locked / unlocked levels
 */
public class LevelActivity extends Activity implements AdapterView.OnItemClickListener {
    private LinkedHashMap<Integer, String> levelScore;
    private GridView gridView;
    private LevelAdapter levelAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_level_selector);

        init();
        initViews();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (true /*levelAdapter.getLevelStates().get(i)*/) {
            Intent intent = new Intent(LevelActivity.this, GameActivity.class);
            intent.putExtra("level", i);
            startActivity(intent);
        }
    }

    private void init() {
        // Instantiate unlocked art per level
        Utils.setLevelUnlock();
        // Load levels from xml and fill data in LevelDataSet
        loadAndParseLevels();
        LinkedHashMap<Integer, ArrayList<String>> levels = LevelDataSet.getLevels();
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        levelScore = new LinkedHashMap<>();
        for (Integer level : levels.keySet())
            levelScore.put(level, sharedPreferences.getString(level.toString(), ""));
    }

    private void initViews() {
        levelAdapter = new LevelAdapter(this, levelScore);
        gridView = (GridView) findViewById(R.id.level_selector_gridview);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(levelAdapter);
    }

    private void loadAndParseLevels() {
        try {
            LevelDataSet.reset();
            // Own class to deserealize using SAX parser
            ParserLevelXml parserLevelXml = new ParserLevelXml();

            // Using SAX parser to parse levels xml
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(parserLevelXml);

            // Get levels xml from raw resources
            InputStream inputStreamLevels = getResources().openRawResource(R.raw.levels);
            xmlReader.parse(new InputSource(inputStreamLevels));
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
    }
}