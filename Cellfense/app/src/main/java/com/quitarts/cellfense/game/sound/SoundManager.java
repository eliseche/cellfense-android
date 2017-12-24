package com.quitarts.cellfense.game.sound;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by alejandro on 29/07/17.
 */

public class SoundManager {
    private static SoundManager instance = null;
    private static HashMap<Sound, Integer> sounds;
    private static HashMap<Music, MediaPlayer> musics;
    private static SoundPool soundPool;

    public enum Sound {
        FIREBALL,
        MACHINE_GUN,
        CANNON,
        LOCK1,
        LOCK2
    }

    public enum Music {
        STRATEGY,
        ACTION
    }

    protected SoundManager() {
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
            init();
        }

        return instance;
    }

    public void playSound(Sound sound) {
        soundPool.play(sounds.get(sound), 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void stopSound(Sound sound) {
        soundPool.stop(sounds.get(sound));
    }

    public void playMusic(Music music, boolean loop) {
        try {
            MediaPlayer mediaPlayer = musics.get(music);
            mediaPlayer.setLooping(loop);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
    }

    public void stopMusic(Music music) {
        MediaPlayer mediaPlayer = musics.get(music);
        mediaPlayer.stop();
    }

    public void stopAllMusic() {
        Collection<MediaPlayer> mediaPlayers = musics.values();
        for (MediaPlayer mediaPlayer : mediaPlayers)
            mediaPlayer.stop();
    }

    public void cleanup() {
        soundPool.release();
        Collection<MediaPlayer> mediaPlayers = musics.values();
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private static void init() {
        initSound();
        initMusic();
    }

    private static void initSound() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        else
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        sounds = new HashMap<>();
        sounds.put(Sound.FIREBALL, soundPool.load(ContextContainer.getContext(), R.raw.fireball, 0));
        sounds.put(Sound.MACHINE_GUN, soundPool.load(ContextContainer.getContext(), R.raw.machine_gun, 0));
        sounds.put(Sound.CANNON, soundPool.load(ContextContainer.getContext(), R.raw.cannon, 0));
        sounds.put(Sound.LOCK1, soundPool.load(ContextContainer.getContext(), R.raw.lock1, 0));
        sounds.put(Sound.LOCK2, soundPool.load(ContextContainer.getContext(), R.raw.lock2, 0));
    }

    private static void initMusic() {
        musics = new HashMap<>();
        musics.put(Music.STRATEGY, MediaPlayer.create(ContextContainer.getContext(), R.raw.defense2));
        musics.put(Music.ACTION, MediaPlayer.create(ContextContainer.getContext(), R.raw.virus2));
    }
}
