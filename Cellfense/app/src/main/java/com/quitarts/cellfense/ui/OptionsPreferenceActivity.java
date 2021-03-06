package com.quitarts.cellfense.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;

/**
 * Preference screen
 */
public class OptionsPreferenceActivity extends PreferenceActivity {
    // TODO: total refactor, move to LinearLayout instead of PreferenceActivity (no flexifility)
    // Music vars
    private static final String OPT_MUSIC = "music";
    private static final boolean OPT_MUSIC_VALUE = true;
    private static final String OPT_MUSIC_VOLUME = "musicVolume";
    private static final int OPT_MUSIC_VOLUME_VALUE = 100;
    // Sound vars
    private static final String OPT_SOUND = "sound";
    private static final boolean OPT_SOUND_VALUE = true;
    private static final String OPT_SOUND_VOLUME = "soundVolume";
    private static final int OPT_SOUND_VOLUME_VALUE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        initViews();
    }

    public static boolean getMusic() {
        return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getContext()).getBoolean(OPT_MUSIC, OPT_MUSIC_VALUE);
    }

    public static int getMusicVolume() {
        return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getContext()).getInt(OPT_MUSIC_VOLUME, OPT_MUSIC_VOLUME_VALUE);
    }

    public static boolean getSound() {
        return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getContext()).getBoolean(OPT_SOUND, OPT_SOUND_VALUE);
    }

    public static int getSoundVolume() {
        return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getContext()).getInt(OPT_SOUND_VOLUME, OPT_SOUND_VOLUME_VALUE);
    }

    private void initViews() {
        int paddingPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 98, getResources().getDisplayMetrics());
        getListView().setPadding(0, paddingPixels, 0, 0);
    }
}