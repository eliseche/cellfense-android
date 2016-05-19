package com.quitarts.cellfense;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Options extends PreferenceActivity {
	private static final String OPT_MUSIC = "music";
	private static final boolean OPT_MUSIC_DEF = true;
	private static final String OPT_SOUND = "sound";
	private static final boolean OPT_SOUND_DEF = true;
	private static final String OPT_MUSIC_VOLUME = "musicVolume";
	private static final int OPT_MUSIC_VOLUME_DEF = 100;
	private static final String OPT_SOUND_VOLUME = "soundVolume";
	private static final int OPT_SOUND_VOLUME_DEF = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		addPreferencesFromResource(R.xml.settings);		
	}
	
	public static boolean getMusic() {
		return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getApplicationContext()).getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
	}
	
	public static boolean getSound() {
		return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getApplicationContext()).getBoolean(OPT_SOUND, OPT_SOUND_DEF);
	}
	
	public static int getMusicVolume() {
		return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getApplicationContext()).getInt(OPT_MUSIC_VOLUME, OPT_MUSIC_VOLUME_DEF);
	}
	
	public static int getSoundVolume() {
		return PreferenceManager.getDefaultSharedPreferences(ContextContainer.getApplicationContext()).getInt(OPT_SOUND_VOLUME, OPT_SOUND_VOLUME_DEF);
	}
}