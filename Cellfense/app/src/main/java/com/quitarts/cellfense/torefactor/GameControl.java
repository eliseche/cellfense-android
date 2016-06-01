
package com.quitarts.cellfense.torefactor;


import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Bullet;
import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.FactoryDrawable.DrawableType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class GameControl {
	private final int TUTORIAL_LEVEL = 0;
	private final int TUTORIAL_TOWER_X = 4;
	private final int TUTORIAL_TOWER_Y = 4;
	private SurfaceHolder surfaceHolder;	
	private GameWorld gameWorld;
	private Hud hud;
	private boolean playing = true;
	private int bonusTime;	
	private Tower addTower;		
	private GameState gameState = GameState.SCREEN1;
	private EnemyState enemyState = EnemyState.UNAVAILABLE;	
	private TutorialState tutorialState;
	private int startActionMoveX;
	private int startActionMoveY;	
	private long holdingDownStartTime;
	private boolean ltaStartShoot;
	private boolean movingExistingTower;
	private Tower tmpMovingTower;
	private Paint rangePaint;
	private Paint blockMessagePaint;
	private Paint tutorialRectPaint;
	private Paint tutorialMessagesPaint;
	private Paint arrowPaint;
	private int acummBlockMessageDt;
	private Config config;
	private SharedPreferences sharedPreferences; 	
	//vars for fps calculation
	private int ticks = 0;
	private long accumDt = 0;		
	private boolean pathBlock;
	private Handler gameSurfaceViewHandler;
	private boolean isGamePaused;
	private int accumWaveKillDt = 0;
	private boolean executeLevel;	
	private boolean sellTower;
	private Paint sellTowerPaint;
	private Drawable tutorialImage;
	private Drawable tutorialImageFinger;	
	private int touchStartPositionX;
	private int touchStartPositionY;
	private boolean isNativePopUpShow;
	private TutorialState prevTutorialState;	
	private int xValueFingerImage;
	private int yValueFingerImage;
	private int yValueOriginalFingerImage;
	private int tutotialFingerImageZoomFactor = 1;
	
	private enum GameState {
	    SCREEN1, SCREEN2
	}
	
	private enum EnemyState {
		UNAVAILABLE, FROZEN, MOVING
	}
	
	public enum TutorialState {
		S1_L1_SCREEN1, S2_L1_SCREEN2, S3_L1_TOWER_DRAGED,
		S4_L1_PLAY_PRESSED, S5_POP_UP_WIN, S5_L2_POPUP_TOWER_POWER,
		S6_L2_SCREEN1, S7_L2_SCREEN1_NEW_ORDE, S8_L2_BEFORE_NEW_ORDE,
		S9_L2_SCREEN2, S9_2_L2_TOWER_DRAGED, S10_L2_PLAY_PRESSED, S11_L2_POPUP_LOSE, S11_L2_POPUP_WIN,
		S12_L3_SCREEN1, S13_L3_SCREEN1_NEW_ORDE, S14_L3_BEFORE_NEW_ORDE, 
		S15_L3_SCREEN2, S16_L3_PLAY_PRESSED, S17_L3_GAME_PAUSE, S18_L3_GAME_RESUME,
		S19_L3_POPUP_LOSE, S19_L3_POPUP_WIN, S20_L4_SCREEN1, S21_L4_SCREEN1_NEW_ORDE,
		S22_L4_BEFORE_NEW_ORDE, S23_L4_SCREEN2,S24_L4_PLAY_PRESSED, S25_L4_GAME_PAUSE,
		S26_L4_GAME_RESUME, S26_L4_POPUP_LOSE, S26_L4_POPUP_WIN, S27_L4_END_TUTORIAL
	}
	
	public class Config {
		public int lives = GameRules.getStartLives();
		public int score = 0;	
		public int resources = 0;
		public int wave = 0;
		public final int max_units = GameRules.getMaxUnits();		
	}
	
	public GameControl(SurfaceHolder pSurfaceHolder, Handler handler, int startLevel) {		
		sharedPreferences = ContextContainer.getContext().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
		Typeface tf = Typeface.createFromAsset(ContextContainer.getContext().getAssets(),"fonts/Discognate.ttf");
		Typeface tf2 = Typeface.createFromAsset(ContextContainer.getContext().getAssets(),"fonts/apexnew_medium.ttf");

		gameSurfaceViewHandler = handler;
		surfaceHolder = pSurfaceHolder;			
		
		blockMessagePaint = new Paint();
		blockMessagePaint.setTextSize(30 * Utils.getScaleFactor());
		blockMessagePaint.setColor(Color.WHITE);
		blockMessagePaint.setAntiAlias(true);
		blockMessagePaint.setTypeface(tf);
		
		sellTowerPaint = new Paint();
		sellTowerPaint.setTextSize(30 * Utils.getScaleFactor());		
		sellTowerPaint.setColor(Color.WHITE);
		sellTowerPaint.setAntiAlias(true);
		sellTowerPaint.setTypeface(tf);
		
		rangePaint = new Paint();	
		rangePaint.setColor(Color.BLUE);			
		rangePaint.setAlpha(25);
		
		tutorialRectPaint = new Paint();
		tutorialRectPaint.setColor(Color.YELLOW);
		tutorialRectPaint.setStyle(Style.STROKE);
		tutorialRectPaint.setAntiAlias(true);
		tutorialRectPaint.setStrokeWidth(3);
		
		arrowPaint = new Paint();
		arrowPaint.setColor(Color.BLUE);
		arrowPaint.setStyle(Style.STROKE);
		arrowPaint.setAntiAlias(true);
		arrowPaint.setStrokeWidth(5);
		
		tutorialMessagesPaint = new Paint();
		tutorialMessagesPaint.setColor(Color.WHITE);
		tutorialMessagesPaint.setAntiAlias(true);
		tutorialMessagesPaint.setTextSize(16 * Utils.getScaleFactor());
		tutorialMessagesPaint.setTypeface(tf2);
		tutorialMessagesPaint.setAlpha(0);
		
		config = new Config();	
		config.wave = startLevel;
		if(startLevel == TUTORIAL_LEVEL){
			tutorialState = TutorialState.S1_L1_SCREEN1;
			this.tutorialImage = FactoryDrawable.createDrawable(DrawableType.TUTORIAL_VS);
			this.tutorialImageFinger = FactoryDrawable.createDrawable(DrawableType.TUTORIAL_FINGER);
			this.yValueFingerImage = Utils.getCanvasHeight() - tutorialImageFinger.getMinimumHeight()/2;
			this.yValueOriginalFingerImage = Utils.getCanvasHeight() - tutorialImageFinger.getMinimumHeight()/2;
		}
	}
	
	public void play() {	
		int FramePerSecondControlValue  = Utils.getFramesPerSecond();
		int dt = 0, fullDt = 0;	
						
		Canvas c = surfaceHolder.lockCanvas(null);
		gameWorld = new GameWorld(c.getWidth(), c.getHeight(), this);
		surfaceHolder.unlockCanvasAndPost(c);		
		hud = new Hud(this);			
		gameSurfaceViewHandler.sendEmptyMessage(Utils.DIALOG_LOADING_OFF_ID);	
		playing = true;
		
		while(playing) {	
			long timeBeforeDraw = System.currentTimeMillis();
			boolean updateFlag = false;
			if(!isGamePaused){
				updateFlag = false;
				fullDt +=dt;
				
					//if running slow force update method
					while(fullDt > FramePerSecondControlValue){
						update(FramePerSecondControlValue);
						fullDt -= FramePerSecondControlValue;
						updateFlag = true;
					}	
			}	
			else
				updateFlag = true;
			
			if(updateFlag && !isNativePopUpShow){				
				synchronized(surfaceHolder) {	
					c = surfaceHolder.lockCanvas(null);	
					draw(c,dt);
					surfaceHolder.unlockCanvasAndPost(c);
				}
			} 
			else {
				try 
				{
					Thread.sleep(FramePerSecondControlValue/2);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			ticks++;			
			dt = (int)(System.currentTimeMillis() - timeBeforeDraw);
			accumDt += dt;			
		}
	}	
	
	public boolean eventActionDown(MotionEvent ev) {		
		/*
		 * User is trying to shoot using LTA
		 */
		if(gameWorld != null) {
			if(gameWorld.isLtaTouch((int)ev.getX(), (int)ev.getY()) 
			&& enemyState == EnemyState.MOVING
			&& config.resources >= GameRules.getLTAPrice()) {
				startActionMoveX = (int)ev.getX();
				startActionMoveY = (int)ev.getY();
				holdingDownStartTime = System.currentTimeMillis();
				ltaStartShoot = true;
			}
		}		
		/*
		 * User is picking a tower
		 */
		if(enemyState == EnemyState.FROZEN) {
			if(hud.hudClick((int)ev.getX(), (int)ev.getY())){
				addTower.setX((int)ev.getX());
				addTower.setY(hud.getTopBoundOfBottomHud() - addTower.getHeight());				
				addTower.setX(addTower.getFixXPositionElement());
				addTower.setY(addTower.getFixYPositionElement());
			}
			if(gameWorld.isTowerTouch((int)ev.getX(), (int)ev.getY())) {
				tmpMovingTower = (Tower)addTower.clone();
				movingExistingTower = true;				
			}
			else {
				movingExistingTower = false;				
			}			
		}
		
		/*
		 * User activate tower special ability (bomb or crazy tower)
		 */
		if(enemyState == EnemyState.MOVING) {
			gameWorld.towerSpecialAbility((int)ev.getX(), (int)ev.getY());
		}
		
		if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S17_L3_GAME_PAUSE){
			touchStartPositionX = (int)ev.getX(); 
			touchStartPositionY = (int)ev.getY();
		}
		return true;		
	}
	
	public boolean eventActionMove(MotionEvent ev) {
		/*
		 * User is sliding a Tower
		 */
		if(addTower != null) {	
			if(hud.isHudSellAreaTouch((int)ev.getX(), (int)ev.getY(), addTower)) 				
				sellTower = true;
			else 
				sellTower = false;		
			
			if(hud.isNotTouching((int)ev.getY())) {
				addTower.setX((int)ev.getX());
				addTower.setY((int)ev.getY() - addTower.getHeight());					
				addTower.setX(addTower.getFixXPositionElement());
				addTower.setY(addTower.getFixYPositionElement());
			}
			else {				
				addTower.setX((int)ev.getX());
				addTower.setY(hud.getTopBoundOfBottomHud() - addTower.getHeight());				
				addTower.setX(addTower.getFixXPositionElement());
				addTower.setY(addTower.getFixYPositionElement());
			}			
			
			if(gameWorld.isPlaceEmpy(addTower)) 			
				addTower.getGraphic().mutate().setColorFilter(null);
			else				
				addTower.getGraphic().mutate().setColorFilter(Color.argb(70, 255, 0, 0), Mode.SRC_IN);					
		}
		/*
		 * LTA shoot is on max power
		 */
		else if(ltaStartShoot && gameState == GameState.SCREEN2 && enemyState == EnemyState.MOVING) {
			 int dy = (int)(startActionMoveY - ev.getY());			
			 if(dy > (Utils.getCanvasHeight() - hud.getTopBoundOfBottomHud())*1.5){
				 if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S17_L3_GAME_PAUSE
							&& gameWorld.isLtaTouch(touchStartPositionX,touchStartPositionY)){
						tutorialState = TutorialState.S18_L3_GAME_RESUME;
						resume();
						shootLTA(ev);
					}
				 else
					 shootLTA(ev);	
			 }
						 
		}		
	        
		return true;
	}
	
	public boolean eventActionUp(MotionEvent ev) {		
		/*
		 * Click button slide down to Screen2
		 */
		if(gameState == GameState.SCREEN1 && enemyState == EnemyState.FROZEN && hud.buttonDownClicked((int)ev.getX(), (int)ev.getY())) {			
			gameState = GameState.SCREEN2;
			
			gameWorld.slideToBottomScreen();
			if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S1_L1_SCREEN1){
				tutorialState = TutorialState.S2_L1_SCREEN2;			
			}
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S8_L2_BEFORE_NEW_ORDE){
				tutorialState = TutorialState.S9_L2_SCREEN2;				
			}	
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S14_L3_BEFORE_NEW_ORDE){
				tutorialState = TutorialState.S15_L3_SCREEN2;				
			}
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S22_L4_BEFORE_NEW_ORDE){
				tutorialState = TutorialState.S23_L4_SCREEN2;				
			}
		}
		/*
		 * Click button slide up to Screen1
		 */
		else if(gameState == GameState.SCREEN2 && enemyState == EnemyState.FROZEN && hud.buttonDownClicked((int)ev.getX(), (int)ev.getY()) && addTower == null) {			
			gameState = GameState.SCREEN1;
			gameWorld.slideToTopScreen();	
			if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S2_L1_SCREEN2){
				tutorialState = TutorialState.S1_L1_SCREEN1;			
			}
		}
		/*
		 * Click button to send next wave
		 */
		else if(gameState == GameState.SCREEN2 &&
				enemyState == EnemyState.FROZEN && 
				gameWorld.worldHaveTower() &&
				hud.hudNextWaveClicked((int)ev.getX(),(int)ev.getY()) ){		
			executeLevel = true;
		}
			
		/*
		 * User is added a tower
		 */
		else if(addTower != null && addTower.getY() < hud.getTopBoundOfBottomHud()) {
			if(hud.isHudSellAreaTouch((int)ev.getX(), (int)ev.getY(), addTower)) {				
				if(movingExistingTower) 
					addResources(addTower.getPrice());
				
				synchronized (addTower) {
					addTower = null;	
				}						
				sellTower = false;
			}
			
			else if(hud.isNotTouching(addTower)) {
				if(gameWorld.isPlaceEmpy(addTower)) {
					if(gameWorld.isNotBlocking(addTower)) {
						if(movingExistingTower) {
							if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S3_L1_TOWER_DRAGED){
								/*
								 * The user is trying to move and existing tower on tutorial mode
								 */
								showTutorialWrongPlaceTower();
								tutorialState = TutorialState.S2_L1_SCREEN2;
							}
							else{
								gameWorld.addTowerToWorld((Tower)addTower.clone());
								addTower.getGraphic().mutate().setColorFilter(null);
							}
						}
						else {
							if(addTower.getPrice() <= config.resources && gameWorld.getTowers() < config.max_units) {
								if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S2_L1_SCREEN2){
									if(isTowerOnTutorialPlace(addTower)){
										tutorialState = TutorialState.S3_L1_TOWER_DRAGED;
										gameWorld.addTowerToWorld((Tower)addTower.clone());
										addTower.getGraphic().mutate().setColorFilter(null);
										config.resources -= addTower.getPrice();
									}
									else
										showTutorialWrongPlaceTower();
								}
								else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S9_L2_SCREEN2){									
										tutorialState = TutorialState.S9_2_L2_TOWER_DRAGED;
										gameWorld.addTowerToWorld((Tower)addTower.clone());
										addTower.getGraphic().mutate().setColorFilter(null);
										config.resources -= addTower.getPrice();									
								}
								else{
									gameWorld.addTowerToWorld((Tower)addTower.clone());
									addTower.getGraphic().mutate().setColorFilter(null);
									config.resources -= addTower.getPrice();
								}
								if(addTower.getType() == Tower.TowerType.TURRET_CAPACITOR)
		                    		SoundManager.playSound(SoundManager.SoundType.LOCK1, false);
		                    
		                    	else if(addTower.getType() == Tower.TowerType.TURRET_TANK)
		                    		SoundManager.playSound(SoundManager.SoundType.LOCK2, false);
								
							}							
						}						
					}
					else {
						if(movingExistingTower) {						
							gameWorld.addTowerToWorld((Tower)tmpMovingTower.clone());
							addTower.getGraphic().mutate().setColorFilter(null);
						}						
						pathBlock = true;
					}						
				}
				else {
					if(movingExistingTower) {						
						gameWorld.addTowerToWorld((Tower)tmpMovingTower.clone());
						addTower.getGraphic().mutate().setColorFilter(null);
					}						
				}				
				synchronized (addTower) {
					addTower = null;	
				}
			}		
		}		
		/*
		 * User is using LTA weapon
		 */		
		else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S5_L2_POPUP_TOWER_POWER
				&& isTutorialVsImageTouch((int)ev.getX(), (int)ev.getY())){
			tutorialState = TutorialState.S6_L2_SCREEN1;
			resume();
		}
		else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S5_L2_POPUP_TOWER_POWER
				&& !isTutorialVsImageTouch((int)ev.getX(), (int)ev.getY())){
			
		}
		else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S17_L3_GAME_PAUSE
				&& gameWorld.isLtaTouch(touchStartPositionX,touchStartPositionY)){
			tutorialState = TutorialState.S18_L3_GAME_RESUME;
			resume();
			shootLTA(ev);
		}
		else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S25_L4_GAME_PAUSE
				&& gameWorld.getUnicTower().getGraphic().getBounds().contains((int)ev.getX(),(int)ev.getY())){
			tutorialState = TutorialState.S26_L4_GAME_RESUME;
			resume();
		}
		else if(ltaStartShoot && gameState == GameState.SCREEN2 && enemyState == EnemyState.MOVING) {
			 shootLTA(ev);
		}
		return true;
	}

	private void shootLTA(MotionEvent ev) {
		 int dx = (int)(ev.getX() - startActionMoveX);
		 int dy = (int)(ev.getY() - startActionMoveY);	
		 if(dx != 0 && dy != 0) {
			 float dt = (System.currentTimeMillis() - holdingDownStartTime);			 
			 //Convert dt to seconds
			 dt = dt/1000;
			 
			 float velX = dx / dt;
			 float velY = dy / dt;
			 
			 Bullet bullet = new Bullet(DrawableType.GUN_LTA_FIRE_SPRITE, 1, 15, 15);
			 int angle = (int)Math.toDegrees(Math.atan2(dx, dy));
			 if(angle < 0) {
				 angle = 180 + Math.abs(angle);
			 }
		     else if(angle >= 0) {
		    	 angle = 180 - angle;
		     }     
			 bullet.setRotationAngle(angle);
			 bullet.start();			 
			 bullet.setX(ev.getX());				
			 bullet.setY(ev.getY());
			 bullet.setDirection(1,1);
			 bullet.setSpeed(velX, velY);
			 
			 gameWorld.addBulletToWorld((Bullet)bullet.clone());
			 SoundManager.playSound(SoundManager.SoundType.FIREBALL, false);
			 config.resources -= GameRules.getLTAPrice();
		 }
		 ltaStartShoot = false;
	}
	
	public void update(int dt) {
		
		if(enemyState == EnemyState.UNAVAILABLE) {
			SoundManager.resumeMusicFade(SoundManager.MusicType.STRATEGY);
			resetGame();
			if(config.wave != TUTORIAL_LEVEL)
				gameWorld.createNewHorde();			
			gameWorld.crittersShouldAdvance(false);
			enemyState = EnemyState.FROZEN;	
			executeLevel = false;

		}
		else if(enemyState == EnemyState.FROZEN) {
			if(executeLevel) {
				if(addTower != null) {
					if(movingExistingTower) {						
						gameWorld.addTowerToWorld((Tower)tmpMovingTower.clone());
						addTower.getGraphic().mutate().setColorFilter(null);						
					}
					synchronized (addTower) {
						addTower = null;					
					}	
				}	
				gameWorld.crittersShouldAdvance(true);				
				enemyState = EnemyState.MOVING;								
				gameState = GameState.SCREEN2;
				gameWorld.slideToBottomScreen();
				SoundManager.pauseMusicFade(SoundManager.MusicType.STRATEGY);
				SoundManager.resumeMusicFade(SoundManager.MusicType.ACTION);
			}
			else 				
				gameWorld.resetTowerAngle();			
		}
		else if(enemyState == EnemyState.MOVING) {
			executeLevel = false;			
			if(gameWorld.isEmptyCritters()) {
				enemyState = EnemyState.UNAVAILABLE;
				gameState = GameState.SCREEN1;		
				
				if(config.lives > 0){
					int intAcumm = ((accumWaveKillDt)/1000);
					bonusTime = (int)((1.0f/(intAcumm*intAcumm)) * 100000.0f);
					gameWorld.slideToTopScreen(10);		
					addScore(bonusTime + ((config.resources /5)*150));
					accumWaveKillDt = 0;					
					ContextContainer.getContext();
					saveScoreLevel();
					pause();
					if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S4_L1_PLAY_PRESSED){
						tutorialState = TutorialState.S5_POP_UP_WIN;
					}	
					else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S10_L2_PLAY_PRESSED){
						tutorialState = TutorialState.S11_L2_POPUP_WIN;
					}	
					else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S18_L3_GAME_RESUME){
						tutorialState = TutorialState.S19_L3_POPUP_WIN;
					}
					else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S26_L4_GAME_RESUME){
						tutorialState = TutorialState.S27_L4_END_TUTORIAL;
					}
					else
					{
						isNativePopUpShow = true;
						gameSurfaceViewHandler.sendEmptyMessage(Utils.DIALOG_LEVEL_PASS_ID);						
					}				
				}
			}
			else
				accumWaveKillDt += dt;	
			
			if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S3_L1_TOWER_DRAGED){
				tutorialState = TutorialState.S4_L1_PLAY_PRESSED;
			}
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S9_2_L2_TOWER_DRAGED){
				tutorialState = TutorialState.S10_L2_PLAY_PRESSED;
			}
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S15_L3_SCREEN2){
				tutorialState = TutorialState.S16_L3_PLAY_PRESSED;
			}
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S23_L4_SCREEN2){
				tutorialState = TutorialState.S24_L4_PLAY_PRESSED;
			}
		}
		
		if (enemyState == EnemyState.FROZEN && gameState == GameState.SCREEN1){
			hud.swithOffNextWaveButton();
		}
		else if (enemyState == EnemyState.FROZEN && gameState == GameState.SCREEN2 && !gameWorld.worldHaveTower()){
			hud.swithOffNextWaveButton();
		}
		else if (enemyState == EnemyState.FROZEN && gameState == GameState.SCREEN2 && gameWorld.worldHaveTower()){
			hud.swithOnNextWaveButton();
		}
		
		gameWorld.update(dt);
		
		hud.update(dt, config.resources, config.lives, config.wave);
		
		if(config.lives <= 0 && enemyState == EnemyState.MOVING) {			
			if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S10_L2_PLAY_PRESSED){
				tutorialState = TutorialState.S11_L2_POPUP_LOSE;
				pause();
			}
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S18_L3_GAME_RESUME){
				tutorialState = TutorialState.S19_L3_POPUP_LOSE;
				pause();
			}
			else if(config.wave == TUTORIAL_LEVEL && tutorialState == TutorialState.S26_L4_GAME_RESUME){
				tutorialState = TutorialState.S26_L4_POPUP_LOSE;
				pause();
			}
			else{
				pause();
				gameSurfaceViewHandler.sendEmptyMessage(Utils.DIALOG_PLAYAGAIN_ID);
			}
		}
		
		tutorialUpdate();
		
		if(prevTutorialState != tutorialState){
			tutorialMessagesPaint.setAlpha(0);
		}
		prevTutorialState = tutorialState;
	}
	
	private void tutorialUpdate(){		
		if(tutorialState == TutorialState.S1_L1_SCREEN1){
			gameWorld.createNewHorde();
		}
		else if(tutorialState == TutorialState.S6_L2_SCREEN1){
			gameWorld.resetGame();
			gameWorld.clearTowers();
			tutorialState = TutorialState.S7_L2_SCREEN1_NEW_ORDE;
		}
		else if(tutorialState == TutorialState.S7_L2_SCREEN1_NEW_ORDE){
			gameWorld.setTutorialWordState(tutorialState);
			gameWorld.clearTowers();
			setResources(50 - gameWorld.getTowers()*GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_CAPACITOR));
			hud.addTowerVisibleTowerType(Tower.TowerType.TURRET_TANK);
			tutorialState = TutorialState.S8_L2_BEFORE_NEW_ORDE;
		}
		else if(tutorialState == TutorialState.S12_L3_SCREEN1){
			gameWorld.resetGame();
			gameWorld.clearTowers();
			tutorialState = TutorialState.S13_L3_SCREEN1_NEW_ORDE;
		}
		else if(tutorialState == TutorialState.S13_L3_SCREEN1_NEW_ORDE){
			gameWorld.setTutorialWordState(tutorialState);
			gameWorld.clearTowers();
			setResources(45- gameWorld.getTowers()*GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_CAPACITOR));
			tutorialState = TutorialState.S14_L3_BEFORE_NEW_ORDE;
		}
		else if(tutorialState == TutorialState.S21_L4_SCREEN1_NEW_ORDE){
			gameWorld.setTutorialWordState(tutorialState);
			gameWorld.clearTowers();
			setResources(45- gameWorld.getTowers()*GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_CAPACITOR));
			tutorialState = TutorialState.S22_L4_BEFORE_NEW_ORDE;
			gameWorld.ltaOff();
		}
		else if(tutorialState == TutorialState.S17_L3_GAME_PAUSE){
			pause();
		}	
		else if(tutorialState == TutorialState.S25_L4_GAME_PAUSE){
			pause();
		}
		else if(tutorialState == TutorialState.S20_L4_SCREEN1){
			gameWorld.resetGame();
			gameWorld.clearTowers();
			tutorialState = TutorialState.S21_L4_SCREEN1_NEW_ORDE;
		}
		else if(tutorialState == TutorialState.S27_L4_END_TUTORIAL){
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_END);
		}
	}
	private void saveScoreLevel() {
		SharedPreferences sp = ContextContainer.getContext().getSharedPreferences("myPrefs", Context.MODE_WORLD_READABLE);
		String scoreSaved = sp.getString(String.valueOf(config.wave), "");
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(scoreSaved == "" || Integer.valueOf(scoreSaved) < config.score){			
			editor.putString(String.valueOf(config.wave),String.valueOf(config.score));
			editor.commit();
		}
		int levelUnlock = Utils.getLevelUnlock(config.wave);
		String result = sp.getString(Utils.UNLOCKED_ART,"0");		
		if(levelUnlock != 0 && levelUnlock > Integer.valueOf(result)){
			editor.putString(Utils.UNLOCKED_ART,String.valueOf(levelUnlock));
			gameSurfaceViewHandler.sendEmptyMessage(Utils.NEW_UNLOCKED_ART);						
			editor.commit();
		}
	}	
	
	public int getResources(){
		return config.resources;
	}
	public boolean isCrittersMoving(){
		if(enemyState == EnemyState.MOVING)
			return true;
		return false;
	}
	
	public void draw(Canvas c, int dt) {	
		if(c != null){
			gameWorld.drawWorld(c);
			if(gameState == GameState.SCREEN2 && enemyState == EnemyState.FROZEN) {
				hud.drawBottomHud(c);
				drawAddingTower(c);
			}		
			hud.draw(c, dt);	
			drawBlockMessagePath(c,dt);
			drawSellIcon(c);
			drawTutorialInfo(c, dt);
		}
	}	
	
	public void pause() {
		isGamePaused = true;
	}	
	
	public void resume(){
		isGamePaused = false;
	}
	
	public void pauseFull(){
		isGamePaused = true;
		isNativePopUpShow = true;
	}
	
	public void resumeFull(){
		isGamePaused = false;
		isNativePopUpShow = false;
	}
	
	public boolean isGamePaused(){
		return isGamePaused;
	}
	
	public void stop(){
		playing = false;
	}
	
	public void addTower(Tower tower) {
		addTower = tower;
	}
	
	public void removeLife() {
		config.lives--;
	}
	
	public void addScore(int score) {
		this.config.score += score;		
	}
	
	public void addResources(int resources) {
		this.config.resources += resources;
		hud.setInitialResources(config.resources);
	}
	
	public void setResources(int resources) {
		config.resources = resources;
		hud.setInitialResources(config.resources);
	}
	
	public int getWave() {
		return config.wave;
	}
	
	public void sendCrittersWave(){	
		if(gameWorld.worldHaveTower())
			executeLevel = true;				
	}
	
	public boolean isOnScreen2(){
		if(gameState == GameState.SCREEN2)
			return true;
		return false;
	}
	
	public int getAccumKillDt(){
		return accumWaveKillDt;
	}
	
	public boolean showNewUnlockedArtMessage() {
		Toast.makeText(ContextContainer.getContext(), ContextContainer.getContext().getResources().getText(R.string.main_unlocked) , Toast.LENGTH_LONG).show();
		return true;
	}
	
	public boolean showGameOverDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.game_over_dialog_message)
    			+ "\n" +
    			ContextContainer.getContext().getResources().getText(R.string.gameover_try_again_dialog_message)
    			);
    	builder.setCancelable(false);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.yes),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				
	    			}
	    		});
	    builder.setNegativeButton(
	    		ContextContainer.getContext().getResources().getText(R.string.no),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				gameSurfaceViewHandler.sendEmptyMessage(Utils.DIALOG_GAMEOVER_ID);
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialOVerDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.game_over_dialog_message));
 	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				gameSurfaceViewHandler.sendEmptyMessage(Utils.DIALOG_GAMEOVER_ID);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialEndPopUp(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_S27_L4_END_TUTORIAL));
 	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				gameSurfaceViewHandler.sendEmptyMessage(Utils.DIALOG_GAMEOVER_ID);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialWrongPlaceTower(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
    	builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_try_again_message_1)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {	    				
	    				resume();
	    				/*
	    				 * if the user tryed to move the existing tower
	    				 */
	    				if(config.resources == 0)
	    					setResources(GameRules.getTowerInitialPrice(Tower.TowerType.TURRET_CAPACITOR));
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialLevel2Lose(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_L2_tip)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				tutorialState = TutorialState.S7_L2_SCREEN1_NEW_ORDE; 	    				
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialLevel3Lose(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_L2_tip)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				tutorialState = TutorialState.S13_L3_SCREEN1_NEW_ORDE; 	    				
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	
	public boolean showTutorialLevel1Win(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_s5_popup_win)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				tutorialState = TutorialState.S5_L2_POPUP_TOWER_POWER; 	    				
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create(); 
    	
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialLevel2Win(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setTitle(ContextContainer.getContext().getResources().getText(R.string.congratulations));
		
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_L2_next_Level)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				tutorialState = TutorialState.S12_L3_SCREEN1; 	    				
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialLevel3Win(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_S19_L3_POPUP_WIN)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				tutorialState = TutorialState.S20_L4_SCREEN1; 	    				
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialLevel4Win(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_S19_L3_POPUP_WIN)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				tutorialState = TutorialState.S27_L4_END_TUTORIAL; 	    				
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showTutorialLevel4Lose(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.tutorial_L2_tip)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.Ok),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				tutorialState = TutorialState.S21_L4_SCREEN1_NEW_ORDE; 	    				
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	    			}
	    		});
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
	
	public boolean showLevelWinDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ContextContainer.getContext());
		builder.setCancelable(false);
		builder.setMessage(
    			ContextContainer.getContext().getResources().getText(R.string.Level_complete_dialog_message)
    			+ "\n" +
    			ContextContainer.getContext().getResources().getText(R.string.score)
    			+ String.valueOf(config.score) 
    			+ "\n" +
    			ContextContainer.getContext().getResources().getText(R.string.Play_Again_dialog_message)
    			);
	    builder.setPositiveButton(
	    		ContextContainer.getContext().getResources().getText(R.string.yes),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				resetGame();
	    				resume();
	    				SoundManager.pauseMusicFade(SoundManager.MusicType.ACTION);
	    				isNativePopUpShow = false;
	           }
	       });
	    builder.setNegativeButton(
	    		ContextContainer.getContext().getResources().getText(R.string.no),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				gameSurfaceViewHandler.sendEmptyMessage(Utils.DIALOG_GAMEOVER_ID);
	    				isNativePopUpShow = false;
	            }
	       });
	    builder.setNeutralButton(
	    		ContextContainer.getContext().getResources().getText(R.string.post_score),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				Message msg = Message.obtain();
	    				msg.what = Utils.POST_SCORE_ID;
	    				msg.arg1 = config.wave;	    				
	    				msg.arg2 = Integer.parseInt(sharedPreferences.getString(String.valueOf(config.wave), ""));	    				
	    				gameSurfaceViewHandler.sendMessage(msg);
	    				isNativePopUpShow = false;
	            }
	       });
	    
    	AlertDialog alert = builder.create();    
    	alert.show();
    	return true;
	}
		
	private void resetGame(){
		config.lives = GameRules.getStartLives();		
		enemyState = EnemyState.UNAVAILABLE;
		gameWorld.slideToBottomScreen();
		gameState = GameState.SCREEN1;	
		gameWorld.resetGame();
		accumWaveKillDt = 0;
		config.score = 0;	
	}
	
	private void drawSellIcon(Canvas c) {
		if(sellTower) {
			String sellMessage = ContextContainer.getContext().getString(R.string.Sell_Tower) + "?";
			c.drawText(sellMessage, Utils.getCanvasWidth() / 2 - sellTowerPaint.measureText(sellMessage) / 2, Utils.getCanvasHeight() / 2 - sellTowerPaint.getTextSize(), sellTowerPaint);
		}
	}
		
	private void drawBlockMessagePath(Canvas c, int dt){
		if(pathBlock){
			acummBlockMessageDt += dt;
			if(acummBlockMessageDt <= 500){
				if(blockMessagePaint.getColor() == Color.WHITE)					
					blockMessagePaint.setColor(Color.RED);				
				else
					blockMessagePaint.setColor(Color.WHITE);
				
				String blockMessage = ContextContainer.getContext().getString(R.string.Block_Message);
				c.drawText(blockMessage, 
						   Utils.getCanvasWidth() / 2 - blockMessagePaint.measureText(blockMessage) / 2,
						   (Utils.getCanvasWidth() / 2) - blockMessagePaint.getTextSize(), 
						   blockMessagePaint);
			}
			else{
				pathBlock = false;
				acummBlockMessageDt = 0;
			}
		}
	}
	
	private void drawAddingTower(Canvas c) {		
		if(addTower != null) {
			synchronized (addTower) {				
				c.drawCircle(addTower.getXCenter(), addTower.getYCenter(), addTower.getRange(), addTower.getRangeShootPaint());
				
				addTower.getGraphic().setBounds(addTower.getFixXPositionElement(), addTower.getFixYPositionElement(), addTower.getFixXPositionElement() + addTower.getWidth(), addTower.getFixYPositionElement() + addTower.getHeight());				
				if(addTower.getType() == Tower.TowerType.TURRET_TANK) {
					BitmapDrawable turretTankBase = addTower.getTurretBase();
					turretTankBase.setBounds(addTower.getGraphic().getBounds());										
					turretTankBase.draw(c);					
				}			
				addTower.getGraphic().draw(c);		
			}						
		}		
	}
	
	private void drawTutorialInfo(Canvas c, int dt){
		if(tutorialState == TutorialState.S1_L1_SCREEN1){
			drawTutorialText(c,ContextContainer.getContext().getResources().getString(R.string.tutorial_S1_SCREEN1),	3);
			drawTutorialText(c,ContextContainer.getContext().getResources().getString(R.string.tutorial_S1_SCREEN1_2), 2);
			drawTutorialText(c,ContextContainer.getContext().getResources().getString(R.string.tutorial_S1_SCREEN1_3), 1);
		}		
		else if(tutorialState == TutorialState.S2_L1_SCREEN2 && gameState == GameState.SCREEN2){			
			/*
			 * Tower position rect
			 */
			int right = Utils.convertXGridToWorld(TUTORIAL_TOWER_X,(int)Utils.getCellSize());
			int top = Utils.convertYGridToWorld(TUTORIAL_TOWER_Y,(int)Utils.getCellSize());
			Rect rect = new Rect(right,top,	right + (int)Utils.getCellSize(),top + (int)Utils.getCellSize());
			tutorialRectPaint.setAlpha(tutorialRectPaint.getAlpha() - 10);			
			c.drawRect(rect, tutorialRectPaint);
			/*
			 * Line to position rect
			 */
			c.drawLine(hud.getTurretCenterX(),
					hud.getTurretCenterY(),
					right + Utils.getCellSize()/2,
					top + (int)Utils.getCellSize(),
					tutorialRectPaint);		
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S2_SCREEN2),2);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S2_SCREEN2_2),1);
			/*
			 * Tower cost enerty position text
			 */
			int arrowWidth = (int)Utils.getCellSize()/2;
			int arrayHeight = (int)(Utils.getCellSize()*0.2);
			int xStartTextValue = (int) (Utils.getCanvasWidth()/2 - tutorialMessagesPaint.measureText(ContextContainer.getContext().getResources().getString(R.string.tutorial_S2_SCREEN2_3))/2 - arrowWidth);
			int xStartArrowEnergyValue = (int) (xStartTextValue + tutorialMessagesPaint.measureText(ContextContainer.getContext().getResources().getString(R.string.tutorial_S2_SCREEN2_3) + 1));
			int xEndArrowX = xStartArrowEnergyValue + arrowWidth;
			c.drawText(ContextContainer.getContext().getResources().getString(R.string.tutorial_S2_SCREEN2_3),
					xStartTextValue,
					tutorialMessagesPaint.getTextSize(),
					tutorialMessagesPaint);
			/*
			 * arrow to energy
			 */
			c.drawLine(xStartArrowEnergyValue,
					tutorialMessagesPaint.getTextSize() - tutorialMessagesPaint.getTextSize()*0.2f,
					xEndArrowX,
					tutorialMessagesPaint.getTextSize() - tutorialMessagesPaint.getTextSize()*0.2f,
					tutorialRectPaint);	
			c.drawLine(xEndArrowX,
					tutorialMessagesPaint.getTextSize() - tutorialMessagesPaint.getTextSize()*0.2f,
					xEndArrowX - Utils.getCellSize()*0.2f,
					tutorialMessagesPaint.getTextSize() - tutorialMessagesPaint.getTextSize()*0.2f - arrayHeight,
					tutorialRectPaint);	
			c.drawLine(xEndArrowX,
					tutorialMessagesPaint.getTextSize() - tutorialMessagesPaint.getTextSize()*0.2f,
					xEndArrowX - Utils.getCellSize()*0.2f,
					tutorialMessagesPaint.getTextSize() - tutorialMessagesPaint.getTextSize()*0.2f + arrayHeight,
					tutorialRectPaint);	
			gameWorld.ltaOff();
		}
		else if(tutorialState == TutorialState.S3_L1_TOWER_DRAGED){
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S3_TOWER_DRAGED),2);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S3_TOWER_DRAGED_2),1);
		}	
		else if(tutorialState == TutorialState.S4_L1_PLAY_PRESSED){
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S4_PLAY_PRESSED),1);
		}
		else if(tutorialState == TutorialState.S5_POP_UP_WIN){
			isNativePopUpShow = true;
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_S5_L1_POPUP_WIN);			
		}
		else if(tutorialState == TutorialState.S5_L2_POPUP_TOWER_POWER){
			tutorialMessagesPaint.setAlpha(255);
			pause();
			int xValue = Utils.getCanvasWidth()/2 - tutorialImage.getMinimumWidth()/2;
			int yValue = Utils.getCanvasHeight()/2 - tutorialImage.getMinimumHeight()/2;
			tutorialImage.setBounds(xValue,
					yValue,
					xValue + tutorialImage.getMinimumWidth(),
					yValue + tutorialImage.getMinimumHeight());
			tutorialImage.draw(c);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_pop_up_vs),5);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_pop_up_vs_1),4);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_pop_up_vs_2),3);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_pop_up_vs_3),2);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_pop_up_vs_4),1);
		}
		else if(tutorialState == TutorialState.S9_2_L2_TOWER_DRAGED && gameState == GameState.SCREEN2){	
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_L2_tower_draged),2);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_L2_tower_draged_1),1);
		}
		else if(tutorialState == TutorialState.S15_L3_SCREEN2){
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S15_L3_SCREEN2),1);
		}
		else if(tutorialState == TutorialState.S16_L3_PLAY_PRESSED){
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S16_L3_PLAY_PRESSED),2);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S16_L3_PLAY_PRESSED_2),1);
		}
		else if(tutorialState == TutorialState.S11_L2_POPUP_LOSE){
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_S11_L2_POPUP_LOSE);			
			isNativePopUpShow = true;
		}
		else if(tutorialState == TutorialState.S11_L2_POPUP_WIN){
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_S11_L2_POPUP_WIN);
			isNativePopUpShow = true;
		}
		else if(tutorialState == TutorialState.S17_L3_GAME_PAUSE){
			int speedAnimationFactor = 6;
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S17_L3_GAME_PAUSE),3);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S17_L3_GAME_PAUSE_2),2);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S17_L3_GAME_PAUSE_3),1);
			xValueFingerImage = (int) (gameWorld.getLTAxCenter() - tutorialImageFinger.getMinimumHeight()/2);
		
			tutorialImageFinger.setBounds(xValueFingerImage - tutotialFingerImageZoomFactor,
					yValueFingerImage - tutotialFingerImageZoomFactor,
					xValueFingerImage + tutorialImageFinger.getMinimumWidth() + tutotialFingerImageZoomFactor,
					yValueFingerImage + tutorialImageFinger.getMinimumHeight() + tutotialFingerImageZoomFactor
					);
			tutorialImageFinger.draw(c);
			if(tutorialImageFinger.getBounds().centerY() > Utils.getCanvasHeight() - Utils.getCellSize()*2){
				yValueFingerImage -= dt/speedAnimationFactor;
			}
			else{
				if(tutotialFingerImageZoomFactor < tutorialImageFinger.getIntrinsicWidth()*15/100){
					tutotialFingerImageZoomFactor += dt/speedAnimationFactor;
				}
				else{
					tutotialFingerImageZoomFactor = 1;	
					yValueFingerImage = yValueOriginalFingerImage;
				}
			}
		}
		else if(tutorialState == TutorialState.S19_L3_POPUP_LOSE){
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_S19_L3_POPUP_LOSE);
			isNativePopUpShow = true;
		}
		else if(tutorialState == TutorialState.S19_L3_POPUP_WIN){
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_S19_L3_POPUP_WIN);
			isNativePopUpShow = true;
		}
		else if(tutorialState == TutorialState.S25_L4_GAME_PAUSE){
			int speedAnimationFactor = 10;
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S25_L4_GAME_PAUSE),3);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S25_L4_GAME_PAUSE_2),2);
			drawTutorialText(c, ContextContainer.getContext().getResources().getString(R.string.tutorial_S25_L4_GAME_PAUSE_3),1);
			xValueFingerImage = (int) gameWorld.getUnicTower().getXCenter()- tutorialImageFinger.getMinimumWidth()/2;
			yValueFingerImage = (int) gameWorld.getUnicTower().getYCenter()- tutorialImageFinger.getMinimumHeight()/2;
			
			tutorialImageFinger.setBounds(xValueFingerImage  - tutotialFingerImageZoomFactor,
					yValueFingerImage  - tutotialFingerImageZoomFactor,
					xValueFingerImage + tutorialImageFinger.getMinimumWidth() + tutotialFingerImageZoomFactor,
					yValueFingerImage + tutorialImageFinger.getMinimumHeight() + tutotialFingerImageZoomFactor
					);
			tutorialImageFinger.draw(c);
			
				if(tutotialFingerImageZoomFactor < tutorialImageFinger.getIntrinsicWidth()*15/100){
					tutotialFingerImageZoomFactor += dt/speedAnimationFactor;
				}
				else{
					tutotialFingerImageZoomFactor = 1;	
				}
			
		}
		else if(tutorialState == TutorialState.S26_L4_POPUP_LOSE){
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_S26_L4_POPUP_LOSE);
			isNativePopUpShow = true;
		}
		else if(tutorialState == TutorialState.S26_L4_POPUP_WIN){
			gameSurfaceViewHandler.sendEmptyMessage(Utils.TUTORIAL_S26_L4_POPUP_WIN);
			isNativePopUpShow = true;
		}
				
		if(tutorialMessagesPaint.getAlpha() < 255){
			if(tutorialMessagesPaint.getAlpha() + dt*2 > 255)
				tutorialMessagesPaint.setAlpha(255);
			else
				tutorialMessagesPaint.setAlpha(tutorialMessagesPaint.getAlpha()+4);
		}
			
	}
	
	private boolean isTutorialVsImageTouch(int x, int y){
		return tutorialImage.getBounds().contains(x, y);
	}
	
	private void drawTutorialText(Canvas c, String message, int space){
		int xStart = (int) (Utils.getCanvasWidth()/2 - tutorialMessagesPaint.measureText(message)/2); 
		c.drawText(message, 
					xStart,
				   (hud.getTopBoundOfBottomHud() - (Utils.getCellSize()/2)* space), 
				   tutorialMessagesPaint);		
	}
	
	public TutorialState getTutorialState(){
		return tutorialState;
	}
	
	public void setTutorialState(TutorialState tState){
		tutorialState = tState;
	}
	
	private boolean isTowerOnTutorialPlace(Tower tower){
		if(tower.getGridPositionX() == TUTORIAL_TOWER_X 
				&& tower.getGridPositionY() == TUTORIAL_TOWER_Y)
			return true;
		return false;				
	}
}