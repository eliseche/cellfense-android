package com.quitarts.cellfense;

import java.util.HashMap;
import java.util.Map;
import android.graphics.drawable.BitmapDrawable;

public class FactoryDrawable {
	private static Map<String, BitmapDrawable> bitmaps = new HashMap<String, BitmapDrawable>();
	
	public static enum DrawableType {
		BUTTON_CAPACITOR, BUTTON_TANK, BUTTON_BOMB, BUTTON_ARROW_UP, BUTTON_ARROW_DOWN, HUD, SPIDER, CATERPILLAR, CHIP_INFECTED, LTA, LTA_BULLET,
		TURRET_CAPACITOR, TURRET_TANK_BASE, TURRET_TANK, TURRET_BOMB, LIFE, RESOURCE, NEXT_WAVE, UPGRADE_LTA, BUTTON_SLOW_WEAPON,
		SCORE_HUD, NEXT_WAVE_OFF, BOMB_CRATER, BATTERY, TUTORIAL_ENEMY, FINGER
	}
	
	public static BitmapDrawable createDrawable(DrawableType typeToBuild) {
		BitmapDrawable bitmap = bitmaps.get(typeToBuild);
		if(bitmap == null) {
			switch(typeToBuild) {
			case BUTTON_CAPACITOR:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.button_capacitor);				 
				break;
			case BUTTON_TANK:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.button_tank);				 
				break;
			case BUTTON_BOMB:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.button_bomb);				 
				break;		
			case BUTTON_ARROW_UP:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.bottom_down_arrow_panel); 
				break;
			case BUTTON_ARROW_DOWN:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.bottom_down_arrow_panel); 
				break;
			case HUD:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.hud); 
				break;
			case SPIDER:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.spider); 
				break;
			case CATERPILLAR:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.caterpillar); 
				break;
			case CHIP_INFECTED:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.chip_infected); 
				break;
			case LTA:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.ltapower); 
				break;
			case LTA_BULLET:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.ltafire); 
				break;
			case TURRET_CAPACITOR:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.turret_capacitor); 
				break;
			case TURRET_TANK_BASE:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.turret_tank_base); 
				break;
			case TURRET_TANK:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.turret_tank); 
				break;
			case TURRET_BOMB:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.turret_bomb); 
				break;
			case LIFE:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.life); 
				break;
			case RESOURCE:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.resource); 
				break;
			case NEXT_WAVE:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.bottom_hud_ready_button); 
				break;
			case UPGRADE_LTA:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.lta_upgrade_icon); 
				break;
			case BUTTON_SLOW_WEAPON:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.tower_slow); 
				break;
			case SCORE_HUD:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.top_hud_panel); 
				break;
			case NEXT_WAVE_OFF:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.bottom_hud_ready_button_push); 
				break;
			case BOMB_CRATER:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.bomb_crater); 
				break;
			case BATTERY:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.battery); 
				break; 
			case TUTORIAL_ENEMY:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.towers_vs_enemies); 
				break; 
			case FINGER:
				bitmap = (BitmapDrawable)ContextContainer.getApplicationContext().getResources().getDrawable(R.drawable.finger); 
				break;
			default:
				break;
			}
			bitmaps.put(typeToBuild.toString(), bitmap);
		}
		return bitmap;		
	}	 
}