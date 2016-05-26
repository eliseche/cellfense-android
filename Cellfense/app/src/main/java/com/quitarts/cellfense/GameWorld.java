package com.quitarts.cellfense;

import java.util.ArrayList;
import java.util.HashMap;

import com.quitarts.cellfense.Critter.CritterType;
import com.quitarts.cellfense.FactoryDrawable.DrawableType;
import com.quitarts.cellfense.GameControl.TutorialState;
import com.quitarts.cellfense.SoundManager.SoundType;
import com.quitarts.cellfense.Tower.TowerType;
import com.quitarts.cellfense.game.LevelDataSet;
import com.quitarts.particles.Explosion;
import com.quitarts.pathfinder.AStarPathFinder;
import com.quitarts.pathfinder.Path;
import com.quitarts.pathfinder.PathFinder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Vibrator;

public class GameWorld {	
	private Bitmap background;		
	private GameControl gameControl;
	private int gameWorldWidth;
	private int gameWorldHeight;	
	private int offSetY;	
	private int deltaPositionY;		
	private ArrayList<Tower> towers = new ArrayList<Tower>();
	private ArrayList<Critter> critters = new ArrayList<Critter>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	private Lta lta;
	private boolean crittersShouldAdvance = false;	
	private GameMap map;
	private PathFinder finder;
	private int gameMapHeight;
	private int gameMapWidth;
	private boolean ltaIsOff = false;
	private boolean resetResources;
	private int offsetGridYWorld;
	
	public GameWorld(int screenWidth, int screenHeight, GameControl gameControl) {
		this.gameControl = gameControl;
		gameWorldWidth = screenWidth;
		gameWorldHeight = screenHeight * 2;
		background = BitmapFactory.decodeResource(ContextContainer
				.getApplicationContext().getResources(), R.drawable.background);
		lta = new Lta(DrawableType.LTA, 1, 15, 100);
		lta.start();
		generateMap();

		resetResources = true;
		offsetGridYWorld = Utils.getGridYOffset();
	}
	
	public void update(int dt) {
		processOffsetY();
		processCritters(dt);
		processTowers(dt);
		processLta(dt);
		proccesBullets(dt);
		proccesExplotions(dt);
	}
	
	public void drawWorld(Canvas c) {				
		drawBackground(c);
		drawCritters(c);
		drawTowers(c);	
		drawLta(c);
		drawBullets(c);
		drawExplotions(c);
		//drawGrid(c);		
	}
	
	private void processOffsetY() {
		offSetY += deltaPositionY;			
		if(offSetY < 0)
			offSetY = 0;		
		else if(offSetY > gameWorldHeight / 2)
			offSetY = gameWorldHeight / 2;	
		Utils.setOffsetY(offSetY);
	}
	
	private void processCritters(int dt) {
		ArrayList<Critter> tmpCritters = (ArrayList<Critter>)critters.clone();	
		
		if(crittersShouldAdvance) {
			for(Critter critter : critters) {
					critter.start();
					critter.advance(dt, offSetY);
					critter.tileAnimationUpdate(dt);
					/*
					 * Debug Info on LogCat
					 */
					//System.out.println("Y: " + (critter.getYcenter() - offSetY) + "Time: " + gameControl.getAccumKillDt());            		
					//System.out.println("Energy: " + critter.lives() +
					//" X:" + critter.getX() + 
					//" Y:" + (critter.getY() - offSetY)
					//);

					if(gameControl.getTutorialState() == TutorialState.S16_L3_PLAY_PRESSED 
							&& (towers.get(0).getGridPositionY() - Utils.convertYWorldToGrid(critter.getY() - offSetY, critter.getHeight()) ==3)){
						gameControl.setTutorialState(TutorialState.S17_L3_GAME_PAUSE);
					}
					else if(gameControl.getTutorialState() == TutorialState.S24_L4_PLAY_PRESSED 
							&& (towers.get(0).getGridPositionY() - Utils.convertYWorldToGrid(critter.getY() - offSetY, critter.getHeight()) ==3)){
						gameControl.setTutorialState(TutorialState.S25_L4_GAME_PAUSE);
					}
			}
			if(tmpCritters.size() > 0) {
				/*
				 * Clean out of screen and dead critters		
				 */
				for(Critter tmpCritter : tmpCritters) {					
					if(tmpCritter.getY() > gameWorldHeight) {
						critters.remove(tmpCritter);
						gameControl.removeLife();
					}
					else if(tmpCritter.lives() <= 0) {
						critters.remove(tmpCritter);						
					}					
				}				
			}					
		}
	}
	
	private void processTowers(int dt) {
		synchronized (towers) {
            for(Tower tower : towers) {  
            	if(tower.getVictim() == null  
            			|| !isOnRange(tower,tower.getVictim())
            			|| tower.getVictim().lives() <= 0){
            		tower.setVictim(null);            		
            		if(tower.getType() == TowerType.TURRET_BOMB) {
            			if(tower.destroy()) {            				
            				ArrayList<Critter> critters = findNearestCritters(tower);
            				((Vibrator)ContextContainer.getApplicationContext().getSystemService(ContextContainer.getApplicationContext().VIBRATOR_SERVICE)).vibrate(300);
            				tower.stopAndResetFrame();            				
            				for(Critter critter : critters) {
            					float damage = GameRules.getDamageEnemy(tower, critter);
            					critter.hit(damage);
        						explosions.add(new Explosion(50, (int)critter.getXcenter(),
        								(int)critter.getYcenter() - offSetY, 0,0));            					
            				}            				            				
            			}            			
            		}
            		else {
            			Critter critter = findNearestCritter(tower);
                		if(critter != null)
                        	tower.setVictim(critter);         			
            		}
            	}
            	if(tower.getVictim() != null){
            		float damage = GameRules.getDamageEnemy(tower,tower.getVictim());
                    tower.aimAndShot(tower.getVictim(), offSetY);                    
                    if(tower.mustShoot()) {
                    	tower.start();
                    	
                    	if(tower.getType() == TowerType.TURRET_CAPACITOR){
                    		SoundManager.playSound(SoundType.MACHINE_GUN, false);
                    	}
                    	else if(tower.getType() == TowerType.TURRET_TANK) {
                    		SoundManager.playSound(SoundType.CANNON, false);                    		
                    	}
                    	tower.getVictim().hit(damage);
                    	tower.justShoot();
                    	float dx = tower.getVictim().getXcenter() - tower.getXcenter();
            			float dy = (tower.getVictim().getYcenter() - offSetY) - tower.getYcenter();
            			float v = (float) Math.sqrt(this.distance(tower,tower.getVictim()));
            	
            			explosions.add(new Explosion(25, (int)tower.getVictim().getXcenter(),
            						(int)tower.getVictim().getYcenter() - offSetY,
            						dx/v,dy/v));
                    	/*
                    	 * Debug Info on Log Cat
                    	 */            
                    	//double dist = distance(tower, tower.getVictim());
                    	//System.out.println("E: " + tower.getVictim().lives() +
                    	//" X:" + tower.getVictim().getXcenter() + 
                    	//" Y:" + (tower.getVictim().getYcenter() - offSetY) + 
                    	//" Dist: " + dist/tower.getRange() +
                    	//" Adt: " + gameControl.getAccumKillDt()
                    	//);
                    	//double dist = distance(tower,tower.getVictim());
                    	//if(dist <= tower.getRange() && critters.size() == 1)
                    	//System.out.println("dist " + dist); 
                    	if(tower.getVictim().lives()<= 0){
    						explosions.add(new Explosion(50, (int)tower.getVictim().getXcenter(),
    								(int)tower.getVictim().getYcenter() - offSetY, 0,0));
                    		
    						critters.remove(tower.getVictim());                    		
                    		tower.setVictim(null);                    		
                    	}
                    }                               
                } 
            	else
            		tower.setVictim(null);             	
                tower.tileAnimationUpdate(dt); 
            }
        }				
	}
	
	private void processLta(int dt) {
		if(!ltaIsOff){
			lta.tileAnimationUpdate(dt);
			lta.setX(Utils.getCanvasWidth() / 2 - (lta.getWidth() / 2));
			lta.setY(gameWorldHeight - (lta.getHeight() + lta.getHeight() / 2) - offSetY);
		}
	}	
	
	private void proccesBullets(int dt) {		
		ArrayList<Bullet> tmpBullets = (ArrayList<Bullet>)bullets.clone();				
				
		if(tmpBullets.size() > 0) {
			for(Bullet tmpBullet : tmpBullets) {
				/*
				 * Clean bullet out of the screen
				 */
				if(tmpBullet.getXcenter() < 0 || tmpBullet.getXcenter() > Utils.getCanvasWidth() ||
				   tmpBullet.getYcenter() < 0 || tmpBullet.getYcenter() > Utils.getCanvasHeight()) {
					bullets.remove(tmpBullet);
				}
								
				/*
				 * Clean bullet that hit a critter
				 */
				for(Critter critter : critters) {
					if(critter.isHit(tmpBullet)) {
						ArrayList<BitmapDrawable> tmpGraphics = critter.getGraphics();
						for(BitmapDrawable bd : tmpGraphics) {
							bd.mutate().setColorFilter(Color.argb(70, 100, 100, 255), Mode.SRC_ATOP);
						}
						critter.getSluggish();						
						bullets.remove(tmpBullet);
					}					
				}				
			}
			
			synchronized (bullets) {
				for(Bullet bullet : bullets) {
					bullet.advance(dt);
					bullet.tileAnimationUpdate(dt);
				}				
			}			
		}			
	}
	
	private void proccesExplotions(int dt){
		ArrayList<Explosion> tmpExplosions = (ArrayList<Explosion>)explosions.clone();			
		synchronized (explosions) {
			for (Explosion explosion : explosions) {			
				explosion.update(dt);
			}
		}
		
		for (Explosion tmpExplosion : tmpExplosions) {			
			if(tmpExplosion.isDead())
				explosions.remove(tmpExplosion);
		}	
	}
	
	private void drawBackground(Canvas c) {				
		c.drawBitmap(background, 0, -offSetY, null);
	}	
	
	private void drawCritters(Canvas c) {		
		synchronized (critters) {
			for(Critter critter : critters) {
				critter.draw(c, offSetY);				
				/*
				 * Debug Info:
				 */
				//c.drawRect(critter.getEnergyBarRectAux(), critter.getEnergyBarPaintAux());
				//drawPathPoints(c, critter);
				//c.drawText(String.valueOf(critter.getX()) + " " + String.valueOf(critter.getY() - offSetY), 
				//	critter.getX(),
				//	critter.getY()- offSetY + critter.getHeight() + 5, 
				//	critter.getEnergyBarPaint());
			}					
		}		
	}
	
	private void drawTowers(Canvas c) {		
		synchronized (towers) {
			for(Tower tower : towers) {				
				tower.getGraphic().setBounds((int)tower.getX(), gameWorldHeight / 2 + (int)tower.getY() - offSetY, (int)tower.getX() + tower.getWidth(), gameWorldHeight / 2 + (int)tower.getY() + tower.getHeight() - offSetY);
			
				if(tower.getType() == TowerType.TURRET_TANK) {
					BitmapDrawable turretTankBase = tower.getTurretBase();
					turretTankBase.setBounds(tower.getGraphic().getBounds());										
					turretTankBase.draw(c);					
				}				
				
				c.save();
                c.rotate(tower.rotAngle, tower.getXcenter(), (gameWorldHeight / 2 + tower.getYcenter() - offSetY));
                tower.getGraphic().draw(c);
                c.restore();               
                              
                if(tower.getType() == TowerType.TURRET_BOMB){
                	tower.drawExplotion(c);
                }
                else
                	c.drawCircle(tower.getXCenterOriginal(), tower.getYCenterOriginal() + (gameWorldHeight / 2)- offSetY, tower.getRange(), tower.getRangeShootPaint());
                
                /*
                 * Debug Info and level tower (deprecated)
                 */
                //c.drawText(String.valueOf(tower.getAccumShootingTime()), tower.getX() + tower.getWidth() - tower.getLevelPaint().measureText(String.valueOf(tower.getLevel())), gameWorldHeight / 2 + tower.getY() + tower.getHeight() - offSetY - tower.getLevelPaint().getTextSize(), tower.getLevelPaint());
                //c.drawText(String.valueOf(tower.getLevel()), tower.getX() + tower.getWidth() - tower.getLevelPaint().measureText(String.valueOf(tower.getLevel())), gameWorldHeight / 2 + tower.getY() + tower.getHeight() - offSetY - tower.getLevelPaint().getTextSize(), tower.getLevelPaint());                
                //c.drawText(String.valueOf(this.acummCombodt),300, 300, tower.getLevelPaint());
                //c.drawText(String.valueOf(this.comboKills),300, 340, tower.getLevelPaint());
			}			
		}	
	}
	
	private void drawLta(Canvas c) {	
		if(gameControl.getResources() >= GameRules.getLTAPrice() && gameControl.isCrittersMoving() && !ltaIsOff){
			lta.getGraphic().setBounds((int)lta.getX(), (int)lta.getY(), (int)lta.getX() + lta.getWidth(), (int)lta.getY() + lta.getHeight());
			lta.getGraphic().draw(c);	
		}
	}
	
	public float getLTAxCenter(){
		return lta.getXcenter();
	}
	
	public float getLTAyCenter(){
		return lta.getYcenter();
	}
	
	public float getTutorialCritterXCenter(){
		for (Critter critter : critters) {
			if(critters.size() == 1)
				return critter.xCenter;
		}
		return 0;
	}
	
	public float getTutorialCritterYCenter(){
		for (Critter critter : critters) {
			if(critters.size() == 1)
				return critter.yCenter - offSetY;
		}
		return 0;
	}
	
	private void drawBullets(Canvas c) {
		synchronized (bullets) {			
			for(Bullet bullet : bullets) {
				c.save();
                c.rotate(bullet.rotAngle, bullet.getXcenter(), (gameWorldHeight / 2 + bullet.getYcenter() - offSetY));
				bullet.getGraphic().setBounds((int)bullet.getX(), gameWorldHeight / 2 + (int)bullet.getY() - offSetY, (int)bullet.getX() + bullet.getWidth(), gameWorldHeight / 2 + (int)bullet.getY() + bullet.getHeight() - offSetY);
				bullet.getGraphic().draw(c);
				c.restore();
			}					
		}
	}
	
	private void drawExplotions(Canvas c){
		for (int i = 0; i < explosions.size(); i++) {
			explosions.get(i).draw(c);			
		}
	}
			
	public void slideToTopScreen() {
		deltaPositionY = -45;
	}

	public void slideToBottomScreen() {
		deltaPositionY = 45;
	}
	
	public void slideToTopScreen(int speed) {
		deltaPositionY = -speed;
	}	
	
	public int getTowers() {
		return towers.size();
	}
	
	public void towerSpecialAbility(int x, int y) {
		synchronized (towers) {
			for(Tower tower : towers) {
				if(tower.getGraphic().getBounds().contains(x, y)) {
					if(tower.getType() == TowerType.TURRET_BOMB && tower.hasCharge()) {
						tower.detonate();											
					}
					else if(tower.getType() != TowerType.TURRET_BOMB  
							&& gameControl.getResources() >= GameRules.getCrazyPopUpPrice()
							&& !tower.isCrazy()){
							tower.goCrazy();	
							gameControl.setResources(gameControl.getResources() - GameRules.getCrazyPopUpPrice());
					}
				}				
			}			
		}		
	}
	
	public void addTowerToWorld(Tower tower) {
		synchronized (towers) {		
			towers.add(tower);
			tower.setOriginalPosition();
			map.setUnit(tower.getGridPositionX(), tower.getGridPositionY() + 1, 1);	
		}		
	}	
	
	public void addBulletToWorld(Bullet bullet) {
		synchronized (bullets) {
			bullets.add(bullet);
		}		
	}	
	
	public void crittersShouldAdvance(boolean value) {
		if(value)
			calcCrittersPath();				
		crittersShouldAdvance = value;		
	}
	
	public void setTutorialWordState(TutorialState ts){
		if(ts == TutorialState.S7_L2_SCREEN1_NEW_ORDE){
			critters.clear();		
			generateMap();
			ltaIsOff = false;
			critters.add(CritterFactory.createCritter(CritterType.SPIDER,1,8));
			critters.add(CritterFactory.createCritter(CritterType.CATERPILLAR,8,8));			
		}
		else if(ts == TutorialState.S13_L3_SCREEN1_NEW_ORDE){
			critters.clear();
			generateMap();
			critters.add(CritterFactory.createCritter(CritterType.SPIDER,4,2));
			critters.add(CritterFactory.createCritter(CritterType.SPIDER,5,8));
		}
		else if(ts == TutorialState.S21_L4_SCREEN1_NEW_ORDE){
			critters.clear();
			generateMap();
			critters.add(CritterFactory.createCritter(CritterType.CATERPILLAR,5,3));
			critters.add(CritterFactory.createCritter(CritterType.SPIDER,5,9));
		}
	}
	
	public Tower getUnicTower(){
		if(towers.size() > 0)
			return towers.get(0);
		return null;
	}
	
	public boolean isPlaceEmpy(Tower tower) {
		boolean emptyPlace = true;
			
		for(Tower towersCollection : towers) {
			if(tower.getGraphic().getBounds().intersect(towersCollection.getGraphic().getBounds())) {				
				emptyPlace = false;
				break;
			}
			else {				
				emptyPlace = true;
			}
		}		
		return emptyPlace;
	}
	
	public boolean isNotBlocking(Tower tower) {

		//System.out.println("X:" + tower.getGridPositionX());
		//System.out.println("Y:" + tower.getGridPositionY());
		map.setUnit(tower.getGridPositionX(), tower.getGridPositionY() +1, 1);
		finder = new AStarPathFinder(map, 500, false);

		Path testPath = finder.findPath(new UnitMover(0), 
				0, 
				0, 
				//3 is the half of de 0-7 posible values, near LTA. 
				3,
				gameMapHeight-1);

		map.setUnit(tower.getGridPositionX(), tower.getGridPositionY() + 1, 0);		
		if(testPath == null)
			return false;								
		return true;		
	}
	
	public boolean isEmptyCritters() {		
		return critters.isEmpty();
	}
	
	public boolean isLtaTouch(int x, int y) {
		return lta.isClicked(x, y);
	}	
	
	public void updateLTA(){
		lta.updateLTA();
	}
	
	public void ltaOff(){
		ltaIsOff = true;
	}
	
	public boolean isTowerTouch(int x, int y) {
		synchronized (towers) {
			for(Tower tower : towers) {
				if(tower.getGraphic().getBounds().contains(x, y)) {
					gameControl.addTower(tower);
					map.setUnit(tower.getGridPositionX(), tower.getGridPositionY() + 1, 0);
					towers.remove(tower);					
					return true;
				}				
			}			
		}
		return false;
	}
	
	public void createNewHorde() {		
		HashMap<Integer, ArrayList<String>> levels = LevelDataSet.getLevels();
		HashMap<Integer, Integer> resources = LevelDataSet.getResources();		
		
		if(gameControl.getWave() <= levels.size()) {
			resetGame();
			if(resetResources){
				gameControl.setResources(resources.get(gameControl.getWave()));
				resetResources = false;
			}
			else
				gameControl.setResources(resources.get(gameControl.getWave()) - calcTowersPrice());
						
			new CritterFactory().createPresetLevel(levels.get(gameControl.getWave()), critters);			
		}		
	}	
	
	public void resetTowerAngle() {
		synchronized (towers) {
			for(Tower tower : towers) {
				if(tower.rotAngle > 0 && tower.rotAngle <= 180) {
					tower.rotAngle -= 2;					
				}
				if(tower.rotAngle > 180 && tower.rotAngle < 360) {
					tower.rotAngle += 2;
				}				
			}			
		}
	}
	
	public boolean worldHaveTower(){
		return towers.size() > 0;
	}
	
	public void resetGame(){		
		synchronized (critters){
			critters.clear();
		}		
		synchronized (bullets) {
			bullets.clear();
		}
		synchronized (towers) {
			for(Tower tower : towers) {				
				if(tower.getType() == TowerType.TURRET_BOMB) {
					tower.resetState();
				}
				tower.setVictim(null);
			}
		}		
		slideToTopScreen(80);
	}	
	
	public void clearTowers(){
		synchronized (towers) {
			towers.clear();
		}	
	}
	
	private int calcTowersPrice() {
		int totalPrice = 0;
		synchronized (towers) {			
			for(Tower tower : towers) {
				totalPrice += tower.getPrice();							
			}
		}	
		return totalPrice;
	}

	private double distance(Tower tower, Critter critter) {		
		double dx = Math.abs(tower.getXCenterOriginal() - critter.getXcenter());
		double dy = Math.abs((gameWorldHeight / 2.0 + tower.getYCenterOriginal()) - (critter.getYcenter()));
		double ndx, ndy;
		
		if (dx < dy) {
			double prop = dx/dy;		
			ndy = Utils.getCellSize()/2;
			ndx = ndy * prop;
		} else {
			double prop = dy/dx;		
			ndx = Utils.getCellSize()/2;
			ndy = ndx * prop;
		}
		return Math.sqrt(dx * dx + dy * dy) - Math.sqrt(ndx * ndx + ndy * ndy);		
	}
	
	private boolean isOnRange(Tower tower, Critter critter){		
		return distance(tower, critter) <= tower.getRange();
	}
	
	private void generateMap() {
		/*
		 * Size map 8 x 12
		 */
		gameMapHeight = 12;
		gameMapWidth = 8;
		map = new GameMap(gameMapWidth, gameMapHeight);
	}
	
	private void calcCrittersPath() {		
		finder = new AStarPathFinder(map, 500, false);
		/*
		 * 3 is the "half" of de 0-7 posible values, near LTA. 
		 */
		
		synchronized (critters) {
			for(Critter critter : critters) {
				critter.setCritterPath(finder.findPath(new UnitMover(0),
						critter.getStartGridPositionX(), 
						0, 						
						0,
						gameMapHeight-1)); 			
			}				
		}	
		
		synchronized (critters) {
			for(Critter critter : critters) {				
				critter.setCritterPath2(finder.findPath(new UnitMover(0),
						critter.getStartGridPositionX(), 
						0,  
						3,
						gameMapHeight-1)); 			
			}				
		}
		
		synchronized (critters) {
			for(Critter critter : critters) {				
				critter.setCritterPath3(finder.findPath(new UnitMover(0),
						critter.getStartGridPositionX(), 
						0,  
						critter.getStartGridPositionX()
						,
						gameMapHeight-1)); 			
			}				
		}
		
		synchronized (critters) {
			for(Critter critter : critters) {				
				critter.setCritterPath4(finder.findPath(new UnitMover(0),
						critter.getStartGridPositionX(), 
						0,  
						gameMapWidth-1,
						gameMapHeight-1)); 			
			}				
		}
		
	}
		
	private Critter findNearestCritter(Tower tower) {
        Critter nearestCritter = null;
        double nearestDist = Double.MAX_VALUE;
        
        for(Critter critter : critters) { 
            double dist = distance(tower, critter);
            
            if(dist <= nearestDist && dist <= tower.getRange()
            		|| (dist <= nearestDist && Math.abs(dist - tower.getRange()) < 0.001)) {
            	nearestDist = dist;
                nearestCritter = critter;
            }
        }		

        if(nearestCritter != null && gameControl.isCrittersMoving()) {
        	return nearestCritter;
        }else {
        	return null;
        }
    }
	
	private ArrayList<Critter> findNearestCritters(Tower tower) {
		ArrayList<Critter> nearestCritters = new ArrayList<Critter>();
        double minDist = tower.getRange();
        for(Critter critter : critters) {
            double dist = distance(tower,critter);
            if(dist < minDist) {
            	nearestCritters.add(critter);
            }
        }
        return nearestCritters;        
    }
	
	private void drawGrid(Canvas c) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		
		int startX = (Utils.getCanvasWidth() / GameMap.WIDTH);
		int jump = startX;
		int startY = Utils.getCanvasHeight();
		int jumpY = (int) (- 1 * Utils.getCellSize());
		
		/*
		 * First and Last X Line
		 */
		c.drawLine(0, 0, Utils.getCanvasWidth(), 0, p);
		c.drawLine(0, Utils.getCanvasHeight()-1, Utils.getCanvasWidth(), Utils.getCanvasHeight()-1, p);
		
		/*
		 * First and Last Y Line
		 */
		c.drawLine(0, 0, 0, Utils.getCanvasHeight(), p);
		c.drawLine(Utils.getCanvasWidth()-1, 0, Utils.getCanvasWidth()-1, Utils.getCanvasHeight(), p);
		
		/*
		 * Draw others x and y grid lines
		 */
		for (int i = 0; i < GameMap.WIDTH; i++) {			
			c.drawLine(startX, 0, startX, Utils.getCanvasHeight(), p);
			startX += jump;
		}
		for (int i = 0; i < (int)(Utils.getCanvasHeight()/GameMap.HEIGHT); i++) {			
			c.drawLine(0, startY, Utils.getCanvasWidth(), startY, p);
			startY += jumpY;
		}		
	}
	
	private void drawPathPoints(Canvas c, Critter critter) {
		/*
		 * Draw red point path
		 */
		int value = 0;
		Paint TextPaint = new Paint();
		TextPaint.setTextSize(15);
		TextPaint.setColor(Color.WHITE);
		while(value < critter.getCritterWorldPath().getLength()) {
			c.drawCircle(critter.getCritterWorldPath().getX(value) + critter.getWidth()/2,
					critter.getCritterWorldPath().getY(value) + critter.getHeight()/2, 
					5, TextPaint);
			c.drawText(String.valueOf(critter.getCritterWorldPath().getX(value)) + 
					 " " + String.valueOf(critter.getCritterWorldPath().getY(value)),
					critter.getCritterWorldPath().getX(value) + 2, 
					critter.getCritterWorldPath().getY(value) + 12, TextPaint);
			value++;
		}
	}
}