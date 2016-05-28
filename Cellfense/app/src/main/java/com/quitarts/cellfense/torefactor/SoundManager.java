package com.quitarts.cellfense.torefactor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.ui.OptionsPreferenceActivity;

public class SoundManager {	
	private static HashMap<SoundType, Integer> soundPoolHash;		
	private static SoundPool soundPool;
	private static HashMap<MusicType, MediaPlayer> mediaPlayerHash;
	private static Thread musicThread;	
	
	public static enum SoundType {
		FIREBALL, MACHINE_GUN, CANNON, LOCK1, LOCK2
	}
	
	public static enum MusicType {
		STRATEGY, ACTION 
	}	
	
	private SoundManager() {			
	}
	
	public static void loadSounds() {	      
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundPoolHash = new HashMap<SoundType, Integer>();
		soundPoolHash.put(SoundType.FIREBALL, soundPool.load(ContextContainer.getContext(), R.raw.fireball, 1));
		soundPoolHash.put(SoundType.MACHINE_GUN, soundPool.load(ContextContainer.getContext(), R.raw.machine_gun, 1));
		soundPoolHash.put(SoundType.CANNON, soundPool.load(ContextContainer.getContext(), R.raw.cannon, 1));
		soundPoolHash.put(SoundType.LOCK1, soundPool.load(ContextContainer.getContext(), R.raw.lock1, 1));
		soundPoolHash.put(SoundType.LOCK2, soundPool.load(ContextContainer.getContext(), R.raw.lock2, 1));
	}	
	
	public static void loadMusic() {		
		mediaPlayerHash = new HashMap<MusicType, MediaPlayer>();
		mediaPlayerHash.put(MusicType.STRATEGY, MediaPlayer.create(ContextContainer.getContext(), R.raw.defense2));
		mediaPlayerHash.put(MusicType.ACTION, MediaPlayer.create(ContextContainer.getContext(), R.raw.virus2));
	}
	
	public static void playSound(SoundType soundType, boolean loopSound) {		
		if(OptionsPreferenceActivity.getSound()) {
			int loop = 0;
			if(loopSound == true) {
				loop = -1;			
			}
			
			soundPool.play(soundPoolHash.get(soundType), OptionsPreferenceActivity.getSoundVolume() / 100.0f, OptionsPreferenceActivity.getSoundVolume() / 100.0f, 1, loop, 1.0f);
		}				
	}	
	
	public static void stopSound(SoundType soundType) {		
		soundPool.stop(soundPoolHash.get(soundType));
	}
	
	public static void playMusic(MusicType musicType, boolean loopMusic, boolean mute, boolean seekRandom) {				
		if(OptionsPreferenceActivity.getMusic()) {
			MediaPlayer mediaPlayer = mediaPlayerHash.get(musicType);			
			if(mute) {
				mediaPlayer.setVolume(0.0f, 0.0f);
			}
			else {
				mediaPlayer.setVolume(OptionsPreferenceActivity.getMusicVolume() / 100.0f, OptionsPreferenceActivity.getMusicVolume() / 100.0f);
			}		
						
			if(seekRandom) {
				Random random = new Random();
	            int start = 0;
	            int end = mediaPlayer.getDuration();	            
	            int range = end - start;
	            int fraction = (int)(range * random.nextDouble());
	            int randomNumber = fraction + start;	            
	            mediaPlayer.seekTo(randomNumber);				
			}
		    
			mediaPlayer.setLooping(loopMusic);		
			mediaPlayer.start();			
		}			
	}
	
	public static void setVolume(MusicType musicType, float level) {
		MediaPlayer mediaPlayer = mediaPlayerHash.get(musicType);
		mediaPlayer.setVolume(level, level);
	}
	
	public static void pauseMusics() {
		if(OptionsPreferenceActivity.getMusic()) {
			Collection c = mediaPlayerHash.values();	    
		    Iterator itr = c.iterator();
		    while(itr.hasNext()) {
		    	MediaPlayer mediaPlayer = (MediaPlayer)itr.next();
		    	mediaPlayer.pause();	    	
		    }		
		}
	}
	
	public static void resumeMusics() {
		if(OptionsPreferenceActivity.getMusic()) {
			Collection c = mediaPlayerHash.values();	    
		    Iterator itr = c.iterator();
		    while(itr.hasNext()) {
		    	MediaPlayer mediaPlayer = (MediaPlayer)itr.next();	    	
		    	mediaPlayer.start();	    	
		    }
		}
	}
	
	public static void pauseMusicFade(MusicType musicType) {
		if(OptionsPreferenceActivity.getMusic()) {
			final MediaPlayer mp = mediaPlayerHash.get(musicType);		
			
			musicThread = new Thread() {
				@Override
				public void run() {
					int waited = 0;
					float volDown = OptionsPreferenceActivity.getMusicVolume();
					try {					
						while(waited < 2500) {							
							if(volDown >= 0) {	
								mp.setVolume(volDown / 100.0f, volDown / 100.0f);																																	
							}									
							else {
								break;															
							}
							sleep(100);
							volDown -= 4;
							waited += 100;								
						}
					}catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}
			};
			musicThread.start();
		}
	}	
	
	public static void resumeMusicFade(MusicType musicType) {
		if(OptionsPreferenceActivity.getMusic()) {
			final MediaPlayer mp = mediaPlayerHash.get(musicType);		
			
			musicThread = new Thread() {			
				@Override			
				public void run() {
					int waited = 0;
					float volDown = 0;				
					try {					
						while(waited < 2500) {							
							if(volDown <= OptionsPreferenceActivity.getMusicVolume()) {
								mp.setVolume(volDown / 100.0f, volDown / 100.0f);																	
							}									
							else {
								break;															
							}
							sleep(100);
							volDown += 4;
							waited += 100;								
						}
					}catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}
			};
			musicThread.start();
		}
	}	
	
	public static void cleanup() {
		try {
			if(musicThread != null) {
				musicThread.join();
			}			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Collection c = mediaPlayerHash.values();	    
	    Iterator itr = c.iterator();
	    while(itr.hasNext()) {
	    	MediaPlayer mediaPlayer = (MediaPlayer)itr.next();
	    	mediaPlayer.release();
	    }	
		
		soundPool.release();		
		soundPoolHash.clear();		
		mediaPlayerHash.clear();		
	}
}