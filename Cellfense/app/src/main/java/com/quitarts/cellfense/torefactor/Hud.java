package com.quitarts.cellfense.torefactor;
import java.util.ArrayList;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.game.object.Button;
import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;
import com.quitarts.cellfense.game.LevelDataSet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Hud {
	private Paint resourceAndLifePaint;
	private Paint scorePaint;
	private Paint wavePaint;
	private BitmapDrawable hud;
	private Button buttonCapacitor;
	private Button buttonTank;
	private Button buttonBomb;
	private Button buttonNextWave;
	private Paint turretPricePaint;
	private GameControl gameControl;
	private int resources;
	private int wave;	
	private ArrayList<String> visibleTowers;
	private Button buttonUpDownHudControl;
	private int slidePanelSpeed = 3;
	private Paint barsPaint;
	private Paint separatorPaint;
	private Drawable battery;
	
	public Hud(GameControl gameControl) {	
		visibleTowers = LevelDataSet.getTowers().get(gameControl.getWave());
		this.gameControl = gameControl;				
		initialize();
	}	
	
	public void update(int dt, int resources, int lives, int wave) {		
		this.resources = resources;	
		this.wave = wave;
	}
	
	public void drawUpperHud(Canvas c, int dt) {		
		//drawResources(c);
		drawRes(c, dt);
	}
	
	public void addTowerVisibleTowerType(Tower.TowerType tt){
		if(tt == Tower.TowerType.TURRET_TANK)
			visibleTowers.add("tt");
	}
	
	private int barLimit;
	public void drawRes(Canvas c, int dt) {
		/*if(resources < lastResources ){
			executeBarEfect = true;
			barLimit = lastResources/5;
		}
		else if(accummBarEfectTime < barEfectTime){
			
		}*/
			battery.setBounds(Utils.getCanvasWidth() - battery.getMinimumWidth(),(int)(6*Utils.getCellSize()/100), Utils.getCanvasWidth(),battery.getMinimumHeight());
			battery.draw(c);
			int barsWidth = (int)(Utils.getCellSize() * 10 / 100);
			int barsHeight = (int)(Utils.getCellSize() * 30 / 100);	
			int separatorHeight = (int)(Utils.getCellSize() * 35 / 100);
			int separator = 0;		
			int bars = gameControl.getResources() / 5;			
				
			if(barLimit > bars){
				barLimit--;
				barsPaint.setStrokeWidth(2);
				barsPaint.setARGB(255, 255, 50, 140);
			}
			else{
				barsPaint.setStrokeWidth(1);
				barsPaint.setStyle(Style.FILL);
				barsPaint.setARGB(255, 102, 102, 255);
			}
			for(int i = 1; i <= barLimit; i++) {			   
				Rect rect = new Rect(battery.getBounds().left - separator, 
						battery.getBounds().top + 1, 
						(battery.getBounds().left - separator - barsWidth), 
						(battery.getBounds().bottom / 2) + (barsHeight / 2));
				c.drawRect(rect, barsPaint);
				if(i % 5 == 0) {				
					Rect rect2 = new Rect(rect.right - (int)(Utils.getCellSize() * 8 / 100 ), 
							battery.getBounds().top,
							rect.right - (int)(Utils.getCellSize() * 5 / 100), 
							(battery.getBounds().bottom / 2) + (separatorHeight / 2));
					c.drawRect(rect2, separatorPaint);
					separator += (int)(Utils.getCellSize() * 23 / 100);
				}
				else {
					separator += (int)(Utils.getCellSize() * 13 / 100);				
				}			
			}		
	}
	
	public void drawBottomHud(Canvas c) {		
		drawBottom(c);
		
		for(String str: visibleTowers) {
			if(str.equals("tc")) {
				drawButtonCapacitor(c);				
			}
			if(str.equals("tt")) {
				drawButtonTank(c);				
			}
			if(str.equals("tb")) {
				drawButtonBomb(c);			
			}
		}
				
	}
		
	public boolean hudClick(int x, int y) {		
		if(buttonCapacitor.isClicked(x, y) && resources >= GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_CAPACITOR)) {
			Tower towerHud = new Tower(DrawableType.GUN_TURRET_CAPACITOR_SPRITE, 1, 7, 30, false);
			towerHud.setX(x);
			towerHud.setY(y);			
			gameControl.addTower(towerHud);
			return true;
		}
		else if(buttonTank.isClicked(x, y) && resources >= GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_TANK)) {
			Tower towerHud = new Tower(DrawableType.GUN_TURRET_TANK_SPRITE, 1, 7, 50, false);
			towerHud.setX(x);
			towerHud.setY(y);			
			gameControl.addTower(towerHud);	
			return true;
		}
		else if(buttonBomb.isClicked(x, y) && resources >= GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_BOMB)) {
			Tower towerHud = new Tower(DrawableType.GUN_TURRET_BOMB_SPRITE, 1, 8, 150, true);
			towerHud.setX(x);
			towerHud.setY(y);			
			gameControl.addTower(towerHud);
			return true;
		} 
		return false;
	}
	
	public boolean buttonDownClicked(int x, int y){
		if(buttonUpDownHudControl.isClicked(x, y)) {			
			return true;
		}
		return false;
	}
	
	public boolean hudNextWaveClicked(int x, int y){
		if(buttonNextWave.isClicked(x, y)) {			
			gameControl.sendCrittersWave();	
			return true;
		}
		return false;
	}

	public boolean isHudSellAreaTouch(int x, int y, Tower tower) {
		Rect r = new Rect(0,
				(int) (Utils.getCanvasHeight() - Utils.getCellSize()), 
				Utils.getCanvasWidth(), 
				Utils.getCanvasHeight());
		if(r.contains(x, y)) {
			return true;
		}
		return false;
	}
	
	public boolean isNotTouching(Tower tower) {					
		if(tower.getGraphic().getBounds().intersect(hud.getBounds())) 			
			return false;
		else 			
			return true;
	}
	
	public boolean isNotTouching(int y) {
		if(y > getTopBoundOfBottomHud()) 		
			return false;
		else 			
			return true;
	}	
	
	public int getTopBoundOfBottomHud() {
		return Utils.getCanvasHeight() - hud.getMinimumHeight();
	}
	
	public void draw(Canvas c, int dt){
		drawUpperHud(c, dt);
		drawUpDownHudControl(c, dt);
		drawButtonNextWave(c, dt);
	}
	
	private void drawBottom(Canvas c) {
		hud.setBounds(c.getWidth() - hud.getMinimumWidth(), c.getHeight() - hud.getMinimumHeight(), c.getWidth(), c.getHeight());
		hud.draw(c);			
	}	

	private void drawButtonCapacitor(Canvas c) {
			buttonCapacitor.setX(c.getWidth() - buttonCapacitor.getWidth());
			buttonCapacitor.setY(c.getHeight() - (buttonCapacitor.getHeight() * 2 - buttonCapacitor.getHeight() / 2));
			buttonCapacitor.getGraphic().setBounds((int)buttonCapacitor.getX(), (int)buttonCapacitor.getY(), (int)buttonCapacitor.getX() + buttonCapacitor.getWidth(), (int)buttonCapacitor.getY() + buttonCapacitor.getHeight());
			if(resources < GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_CAPACITOR))
				buttonCapacitor.getGraphic().setAlpha(80);
			else
				buttonCapacitor.getGraphic().setAlpha(255);
			buttonCapacitor.getGraphic().draw(c);
	}
		
	private void drawButtonTank(Canvas c) {
		buttonTank.setX(c.getWidth() - buttonTank.getWidth() * 2);
		buttonTank.setY(c.getHeight() - (buttonTank.getHeight() * 2 - buttonTank.getHeight() / 2));
		buttonTank.getGraphic().setBounds((int)buttonTank.getX(), (int)buttonTank.getY(), (int)buttonTank.getX() + buttonTank.getWidth(), (int)buttonTank.getY() + buttonTank.getHeight());
		if(resources < GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_TANK))
			buttonTank.getGraphic().setAlpha(80);
		else
			buttonTank.getGraphic().setAlpha(255);
		buttonTank.getGraphic().draw(c);				
	}
		
	private void drawButtonBomb(Canvas c) {
		buttonBomb.setX(c.getWidth() - buttonBomb.getWidth() * 3);
		buttonBomb.setY(c.getHeight() - (buttonBomb.getHeight() * 2 - buttonBomb.getHeight() / 2));
		buttonBomb.getGraphic().setBounds((int)buttonBomb.getX(), (int)buttonBomb.getY(), (int)buttonBomb.getX() + buttonBomb.getWidth(), (int)buttonBomb.getY() + buttonBomb.getHeight());
		if(resources < GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_BOMB))
			buttonBomb.getGraphic().setAlpha(80);
		else
			buttonBomb.getGraphic().setAlpha(255);
		buttonBomb.getGraphic().draw(c);
	}
	
	public float getTurretCenterX(){
		return this.buttonCapacitor.getXcenter();
	}
	
	public float getTurretCenterY(){
		return this.buttonCapacitor.getYcenter();
	}
	
	public void pressOnNextWaveButton(){
		buttonNextWave.setGraphicObject(DrawableType.HUD_READY_PUSHED);
	}
	
	public void pressOffNextWaveButton(){
		buttonNextWave.setGraphicObject(DrawableType.HUD_READY);
	}	
	
	public void drawButtonNextWave(Canvas c,int dt) {	
		if(!gameControl.isCrittersMoving()&& buttonUpDownHudControl.getX() < 0)
			buttonNextWave.setX(buttonUpDownHudControl.getGraphic().getBounds().right);		
		else if(gameControl.isCrittersMoving() && buttonNextWave.getX()+ buttonNextWave.getWidth() > 0)
			buttonNextWave.setX(buttonNextWave.getX() - 1*(dt/slidePanelSpeed));		
		else if(gameControl.isCrittersMoving() && buttonNextWave.getX()+ buttonNextWave.getX() < 0)
			return;		
		else
			buttonNextWave.setX(buttonNextWave.getWidth());
		
		buttonNextWave.setY(Utils.getCanvasHeight() - (buttonNextWave.getHeight()));
		buttonNextWave.getGraphic().setBounds((int)buttonNextWave.getX(), (int)buttonNextWave.getY(), (int)buttonNextWave.getX() + buttonNextWave.getWidth(), (int)buttonNextWave.getY() + buttonNextWave.getHeight());
		buttonNextWave.getGraphic().draw(c);		
	}
	
	public void swithOffNextWaveButton() {	
		buttonNextWave.getGraphic().setAlpha(130);	
	}	
	
	public void swithOnNextWaveButton() {	
		buttonNextWave.getGraphic().setAlpha(255);	
	}
	
	public void drawUpDownHudControl(Canvas c, int dt) {
		if(!gameControl.isCrittersMoving()&& buttonUpDownHudControl.getX() < 0)
			buttonUpDownHudControl.setX(buttonUpDownHudControl.getX() + 1*(dt/slidePanelSpeed));		
		else if(gameControl.isCrittersMoving() && buttonUpDownHudControl.getX()+ buttonUpDownHudControl.getWidth()*2 >= 0)
			buttonUpDownHudControl.setX(buttonUpDownHudControl.getX() - 1*(dt/slidePanelSpeed));		
		else if(gameControl.isCrittersMoving() && buttonUpDownHudControl.getX()+ buttonUpDownHudControl.getX()  < 0)
			return;		
		else
			buttonUpDownHudControl.setX(0);
		
		buttonUpDownHudControl.setY(Utils.getCanvasHeight() - (buttonUpDownHudControl.getHeight()));
		buttonUpDownHudControl.getGraphic().setBounds((int)buttonUpDownHudControl.getX(), (int)buttonUpDownHudControl.getY(), (int)buttonUpDownHudControl.getX() + buttonUpDownHudControl.getWidth(), (int)buttonUpDownHudControl.getY() + buttonUpDownHudControl.getHeight());
		buttonUpDownHudControl.getGraphic().draw(c);
	}
	
	public void setInitialResources(int res){		
		barLimit = res/5;		
	}	
	
	private void initialize() {
		hud = FactoryDrawable.createDrawable(DrawableType.HUD);
		hud.setAlpha(80);
		buttonCapacitor = new Button(DrawableType.GUN_TURRET_CAPACITOR);
		Utils.setCellSize(buttonCapacitor.getHeight());
		buttonTank = new Button(DrawableType.GUN_TURRET_TANK);
		buttonBomb = new Button(DrawableType.GUN_TURRET_BOMB);
		buttonNextWave = new Button(DrawableType.HUD_READY);
		buttonNextWave.setX(buttonNextWave.getWidth());
		buttonNextWave.setY(Utils.getCanvasHeight() - (buttonNextWave.getHeight()));
		Typeface tf = Typeface.createFromAsset(ContextContainer.getContext().getAssets(),"fonts/Discognate.ttf");
		resourceAndLifePaint = new Paint();
		resourceAndLifePaint.setTypeface(tf);
		resourceAndLifePaint.setTextSize(15 * Utils.getScaleFactor());
		resourceAndLifePaint.setColor(Color.WHITE);
		resourceAndLifePaint.setAntiAlias(true);		
		scorePaint = new Paint();		
		scorePaint.setTypeface(tf);
		scorePaint.setTextSize(18 * Utils.getScaleFactor());
		scorePaint.setColor(Color.WHITE);
		scorePaint.setAntiAlias(true);	
		wavePaint = new Paint();
		wavePaint.setTypeface(tf);
		wavePaint.setTextSize(13 * Utils.getScaleFactor());
		wavePaint.setColor(Color.WHITE);
		wavePaint.setAntiAlias(true);
		turretPricePaint = new Paint();
		turretPricePaint.setTextSize(10 * Utils.getScaleFactor());
		turretPricePaint.setColor(Color.WHITE);
		turretPricePaint.setAntiAlias(true);
		buttonUpDownHudControl = new Button(DrawableType.HUD_ARROW);
		barsPaint = new Paint();       
		barsPaint.setStyle(Style.FILL);
		barsPaint.setARGB(255, 102, 102, 255);		
		separatorPaint = new Paint();       
		separatorPaint.setStyle(Style.FILL);
		separatorPaint.setARGB(255, 128, 141, 128);		
		battery = FactoryDrawable.createDrawable(DrawableType.HUD_BATTERY);
	}
}