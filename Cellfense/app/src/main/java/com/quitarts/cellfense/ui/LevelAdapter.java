package com.quitarts.cellfense.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quitarts.cellfense.R;

import java.util.LinkedHashMap;

/**
 * GridView adapter
 */
public class LevelAdapter extends BaseAdapter {
    private Context context;
    private LinkedHashMap<Integer, String> levelScore;
    private Typeface typefaceLevel;
    private Typeface typefaceScore;
    private LinkedHashMap<Integer, Boolean> levelStates = new LinkedHashMap<>();
    private int futureUnlockedLevels = 0;
    private static final int MAX_FUTURE_UNLOCKED_LEVELS = 2;

    public LevelAdapter(Context context, LinkedHashMap<Integer, String> levelScore) {
        this.context = context;
        this.levelScore = levelScore;
        this.typefaceLevel = Typeface.createFromAsset(context.getAssets(), "fonts/tseries_c.ttf");
        this.typefaceScore = Typeface.createFromAsset(context.getAssets(), "fonts/apexnew_medium.ttf");
    }

    public LinkedHashMap<Integer, Boolean> getLevelStates() {
        return levelStates;
    }

    @Override
    public int getCount() {
        return levelScore.size();
    }

    @Override
    public String getItem(int i) {
        return levelScore.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.level_selector_item, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.level = (TextView) view.findViewById(R.id.level_selector_item_level);
            viewHolder.level.setTypeface(typefaceLevel);

            viewHolder.score = (TextView) view.findViewById(R.id.level_selector_item_score);
            viewHolder.score.setTypeface(typefaceScore);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        int level = i;
        String score = getItem(i);

        if (isUnlocked(level)) {
            viewHolder.level.setText(String.valueOf(level));
            viewHolder.score.setText(score);
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.menu_button));
        } else {
            viewHolder.level.setText("");
            viewHolder.score.setText("");
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.menu_lock));
        }

        return view;
    }

    private boolean isUnlocked(int level) {
        // Check if level state exists and return state (locked or unlocked)
        if (levelStates.containsKey(level))
            return levelStates.get(level);

        // If level has score, it's unlocked
        if (!levelScore.get(level).equals("")) {
            levelStates.put(level, true);

            return true;
        }
        // If level hasn't score, it's locked, unlock two more levels
        else if (futureUnlockedLevels < MAX_FUTURE_UNLOCKED_LEVELS) {
            futureUnlockedLevels++;
            levelStates.put(level, true);

            return true;
        }

        // Level locked
        levelStates.put(level, false);

        return false;
    }

    static class ViewHolder {
        TextView level;
        TextView score;
    }
}
