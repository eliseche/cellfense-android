package com.quitarts.cellfense.torefactor;

import java.util.Random;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;
import com.quitarts.cellfense.game.object.base.TileAnimation;

public class Tower extends TileAnimation {
	private int gridPositionX;
	private int gridPositionY;	
	private int shootingTime;
	private float shootingRange;
	private int price;
	private int acummShootingTime;
	private Paint levelPaint;
	private Paint rangeShootPaint;	
	private TowerType type;
	private BitmapDrawable turretBase;
	private Critter victim;
	private boolean isDetonated = false;
	private float timeFirstExplotion = 300;
	private float accumExplotsionTime = 0;
	private MaskFilter bombBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	private float explosionRange;
	private Paint explosionPaint;
	private boolean isTheExplotionActive = false;
	private boolean isCrazy;	
	private int crazyTime = 2000;
	private int accumCrazyTime;
	private float xOriginalPosition;
	private float yOriginalPosition;
	private int numberOfExplosions = 1;
	
	
	public static enum TowerType {
		TURRET_CAPACITOR, TURRET_TANK, TURRET_BOMB
	}
	
	public Tower(DrawableType drawableType, int tileRows, int tileColumns, int frameSkipDelay, boolean repeat) {
		super(drawableType, tileRows, tileColumns, frameSkipDelay, repeat);
		
		if(drawableType == DrawableType.GUN_TURRET_CAPACITOR_SPRITE) {
			type = TowerType.TURRET_CAPACITOR;			
		}
		
		if(drawableType == DrawableType.GUN_TURRET_TANK_SPRITE) {
			type = TowerType.TURRET_TANK;
			turretBase = FactoryDrawable.createDrawable(DrawableType.GUN_TURRET_TANK_BASE);
		}		
		
		if(drawableType == DrawableType.GUN_TURRET_BOMB_SPRITE) {
			type = TowerType.TURRET_BOMB;			
		}
		
		initialize();		
	}
	
	public TowerType getType() {
		return type;
	}
	
	public BitmapDrawable getTurretBase() {
		return turretBase;
	}	
	
	public int getFixXPositionElement() {
		return ((int)getX() / getWidth()) * getWidth();
	}
	    
	public int getFixYPositionElement() {
		return ((int)getY() / getHeight()) * getHeight();
	}	
	
	public void resetFrame() {
		currentFrame = 0;
	}
	
	public float getRange() {
		return shootingRange;
	}
	
	public void setShootingRange(float shootingRange) {
		this.shootingRange = shootingRange;
	}
	
	public int getPrice() {
		return price;
	}
	
	public Paint getLevelPaint() {
		return levelPaint;		
	}
	
	public Paint getRangeShootPaint() {
		return rangeShootPaint;		
	}	
		
	public void setX(float value) {
		super.setX(value);
		gridPositionX = Utils.convertXWorldToGrid(getX(), getWidth());
	}	
	
	public void setY(float value){
		super.setY(value);
		gridPositionY = Utils.convertYWorldToGrid(getY(),getHeight());
	}
	
	public int getGridPositionX() {
		return gridPositionX;
	}
	
	public int getGridPositionY() {
		return gridPositionY;
	}
	
	public void detonate() {
		this.isDetonated = true;
		this.isTheExplotionActive = true;
		this.numberOfExplosions--;
	}
	
	public boolean hasCharge(){
		if(this.numberOfExplosions >0)
			return true;
		return false;
	}
	
	public boolean destroy() {
		if(isDetonated){
			isDetonated = false;
			return true;
		}
		return false;
	}
	
	private void processExplosion(int dt){		
		if(isTheExplotionActive){	
			accumExplotsionTime += dt;
			if(accumExplotsionTime < timeFirstExplotion){
				this.rangeShootPaint.setStyle(Style.FILL);
				this.rangeShootPaint.setColor(Color.argb(190,0, 0, 125));
				this.rangeShootPaint.setAlpha(155);                		 
				//this.rangeShootPaint.setMaskFilter(bombBlur);
				this.explosionPaint.setStyle(Style.STROKE);
				this.explosionPaint.setColor(Color.rgb(0, 0, 60));
				this.explosionPaint.setAlpha(255);
				this.explosionPaint.setMaskFilter(bombBlur);
				this.explosionPaint.setStrokeWidth(Utils.getCellSize()/2);
			}
			else{
				setTileAnimation(DrawableType.GUN_TURRET_BOMB_CRATER, 1, 1, 0, false);
				if(rangeShootPaint.getAlpha() > 0){
					int alphaValue = rangeShootPaint.getAlpha() - 10;
					if(alphaValue < 10)
						this.rangeShootPaint.setAlpha(0);
					else
						this.rangeShootPaint.setAlpha(alphaValue);
				}
				else if(explosionRange <= 0){
					this.isTheExplotionActive = false;
					configRangeShootPaint();
				}
				explosionRange -= 3;				
			}
		}
	
	}
	
	private void processCrazyBehavior(int dt){
		rangeShootPaint.setColor(Color.rgb(180, 0, 0));		
		accumCrazyTime += dt;
		shootingTime = GameRules.getTowerInitialShootingTime(type)/3;
		if(accumCrazyTime <= crazyTime){
			this.x = (this.xOriginalPosition + (float)((new Random()).nextInt(3) - 1));
			this.y = (this.yOriginalPosition + (float)((new Random()).nextInt(3) - 1));
			rangeShootPaint.setStrokeWidth(2);
		}
		else{
			shootingTime = GameRules.getTowerInitialShootingTime(type);
			rangeShootPaint.setColor(Color.rgb(0, 120, 0));
			rangeShootPaint.setStrokeWidth(1);
			this.x = this.xOriginalPosition;
			this.y = this.yOriginalPosition;
			accumCrazyTime = 0;
			isCrazy = false;
		}	
	}
	
	public void drawExplotion(Canvas c){
		if(isTheExplotionActive){
			c.drawCircle(getXcenter(), getYcenter() + Utils.getCanvasHeight() - Utils.getOffsetY(), getRange(), getRangeShootPaint());
			c.drawCircle(getXcenter(), getYcenter() + Utils.getCanvasHeight() - Utils.getOffsetY(), explosionRange, explosionPaint);			
		}
		else
			c.drawCircle(getXcenter(), getYcenter() + Utils.getCanvasHeight() - Utils.getOffsetY(), getRange(), getRangeShootPaint());
	}
	
	public void resetState() {	
		this.numberOfExplosions = 1;
		this.isDetonated = false;
		explosionRange =  GameRules.getTowerInitialShootingRange(type, getHeight()) - Utils.getCellSize()/2;
		accumExplotsionTime = 0;
		configRangeShootPaint();

		setTileAnimation(DrawableType.GUN_TURRET_BOMB_SPRITE, 1, 8, 150, true);
		this.start();
	}
	
	public boolean isCrazy(){
		return isCrazy;
	}
	
	public void aimAndShot(Critter critter, int offsetY) {        
	    try {	    	
	    	double dx = critter.getXcenter() - getXcenter();
	        double dy = (critter.getYcenter() - offsetY) - (Utils.getCanvasHeight() + getYcenter() - offsetY);	                    
	        int angle = (int)Math.toDegrees(Math.atan2(dx, dy));                
	        
	        if(angle < 0) {
	        	angle = 180 + Math.abs(angle);
	        }
	        else if(angle >= 0) {
	        	angle = 180 - angle;
	        }     
	        	        
	        rotAngle = angle;                
	    }catch (ArithmeticException ex) {}
	}	
	
	public void tileAnimationUpdate(int dt) {
		super.tileAnimationUpdate(dt);
		acummShootingTime += dt;
		
		if(!(victim == null) && victim.lives() <= 0)
			victim = null;
		if(type == TowerType.TURRET_BOMB)
			processExplosion(dt);
		if(isCrazy){
			processCrazyBehavior(dt);
		}
			
	}
	
	public int getAccumShootingTime() {
		return acummShootingTime;
	}
	
	public void justShoot(){
		acummShootingTime = 0;
	}
	
	public boolean mustShoot() {			
		return acummShootingTime >= shootingTime;
	}	
	
	public Critter getVictim(){
		return victim;
	}
	
	public void setVictim(Critter cr){
		victim = cr;
	}
	
	public void goCrazy(){
		isCrazy = true;
	}
	
	public void setOriginalPosition(){
		xOriginalPosition = this.x;
		yOriginalPosition = this.y;
	}
	
	private void initialize() {
		shootingTime = GameRules.getTowerInitialShootingTime(type);
		shootingRange = GameRules.getTowerInitialShootingRange(type, getHeight());
		explosionRange =  GameRules.getTowerInitialShootingRange(type, getHeight()) - Utils.getCellSize()/2;
		price = GameRules.getTowerInitialPrice(type);
		acummShootingTime = shootingTime;
		
		levelPaint = new Paint();		
		levelPaint.setColor(Color.WHITE);		
		levelPaint.setAntiAlias(false);
		levelPaint.setTextSize(8 * Utils.getScaleFactor());
		
		configRangeShootPaint();
		
		explosionPaint = new Paint();		
		explosionPaint.setColor(Color.rgb(0, 125, 0));
		explosionPaint.setAntiAlias(false);
		explosionPaint.setStyle(Style.STROKE);
	}

	private void configRangeShootPaint() {
		rangeShootPaint = new Paint();		
		rangeShootPaint.setAlpha(255);
		rangeShootPaint.setAntiAlias(false);
		rangeShootPaint.setStyle(Style.STROKE);
		
		if(type != TowerType.TURRET_BOMB)			
			rangeShootPaint.setColor(Color.rgb(0, 120, 0));		
		else //is a BombTower
			rangeShootPaint.setColor(Color.rgb(0, 0, 125));
	}
	
	public float getXCenterOriginal(){
		return xOriginalPosition + Utils.getCellSize() / 2.0f;
	}
	
	public float getYCenterOriginal(){
		return yOriginalPosition + Utils.getCellSize() / 2.0f;
	}
}