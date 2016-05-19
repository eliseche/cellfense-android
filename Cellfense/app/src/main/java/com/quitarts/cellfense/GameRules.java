package com.quitarts.cellfense;

import com.quitarts.cellfense.Critter.CritterType;
import com.quitarts.cellfense.Tower.TowerType;

public class GameRules {
	
	public static float getDamageEnemy(Tower tower, Critter critter) {
		
		if(tower.getType() == TowerType.TURRET_CAPACITOR && critter.getEnemyType() == CritterType.SPIDER) {
			return 13f;			
		}
		if(tower.getType() == TowerType.TURRET_CAPACITOR && critter.getEnemyType() == CritterType.CATERPILLAR) {
			return 4.15f;			
		}
		if(tower.getType() == TowerType.TURRET_CAPACITOR && critter.getEnemyType() == CritterType.CHIP) {
			return 5;			
		}			
		if(tower.getType() == TowerType.TURRET_TANK && critter.getEnemyType() == CritterType.SPIDER) {
			return 16.666f;			
		}
		if(tower.getType() == TowerType.TURRET_TANK && critter.getEnemyType() == CritterType.CATERPILLAR) {
			return 20;			
		}
		if(tower.getType() == TowerType.TURRET_TANK && critter.getEnemyType() == CritterType.CHIP) {
			return 5;			
		}		
		if(tower.getType() == TowerType.TURRET_BOMB && critter.getEnemyType() == CritterType.SPIDER) {
			return 100;			
		}
		if(tower.getType() == TowerType.TURRET_BOMB && critter.getEnemyType() == CritterType.CATERPILLAR) {
			return 100;			
		}
		if(tower.getType() == TowerType.TURRET_BOMB && critter.getEnemyType() == CritterType.CHIP) {
			return 100;			
		}		
		
		return 0;
	}
	
	public static void setShootingRange(Tower tower) {
		if(tower.getType() == TowerType.TURRET_CAPACITOR){ 
			tower.setShootingRange((Utils.getCellSize() * 1.8f));
			return;
		}
		
		if(tower.getType() == TowerType.TURRET_TANK) {
			tower.setShootingRange((Utils.getCellSize() * 1.8f));
			return;						
		}		
	}
	
	public static int getLtaDamage() {
		return 10;
	}
	
	public static int getLTAUpgradePrice(){
		return 10;
	}
	
	public static int getTowerInitialShootingTime(TowerType type) {
		if(type == TowerType.TURRET_CAPACITOR) {
			return 500;			
		}			 			
		if(type == TowerType.TURRET_TANK) {
			return 1300;
		}		
		return 0;
	}	
	
	public static float getTowerInitialShootingRange(TowerType type, int height) {
		if(type == TowerType.TURRET_CAPACITOR) {
			return (Utils.getCellSize() * 1.8f);
		}			
		if(type == TowerType.TURRET_TANK) {
			return (Utils.getCellSize() * 1.8f);
		}		
		if(type == TowerType.TURRET_BOMB) {
			return (Utils.getCellSize() * 1.8f);
		}
		return 0;
	}
	
	public static int getTowerInitialPrice(TowerType type) {
		if(type == TowerType.TURRET_CAPACITOR) {
			return 25;
		}
		if(type == TowerType.TURRET_TANK) {
			return 25;
		}		
		if(type == TowerType.TURRET_BOMB) {
			return 50;
		}
		return 0;
	}
	
	public static int getTimeBar() {
		return 20000;
	}
	
	public static int getComboTime(){
		return 4000;
	}	
	
	public static int getStartLives(){
		return 1;
	}
	
	public static int getMaxUnits(){
		return 10;
	}
	
	public static float getCrittersStartSpeed(CritterType ct) {
		if(ct == CritterType.SPIDER) {
			return 1.4f;			
		}
		else if(ct == CritterType.CATERPILLAR) {
			return 1.0f;			
		}
		else if(ct == CritterType.CHIP) {
			return 3.0f;
		}
		return 1.4f;
	}
	
	public static int getBonusTimeFactor(){
		return 100;
	}
	
	public static float getBUGSpeedXFactor(){
		return 15;
	}
	
	public static int getLTAPrice(){
		return 5;
	}
	
	public static int getCrazyPopUpPrice(){
		return 10;
	}
	
	public static int getSlowCritterTime(){
		return 3000;
	}	
}