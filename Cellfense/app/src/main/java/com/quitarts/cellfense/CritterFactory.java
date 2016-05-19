package com.quitarts.cellfense;

import java.util.ArrayList;
import com.quitarts.cellfense.Critter.CritterType;
import com.quitarts.cellfense.FactoryDrawable.DrawableType;

public class CritterFactory {
	Critter critter;
	
	public Critter createRandomLevel(DrawableType drawableType) {
		if(drawableType == DrawableType.SPIDER) {
			critter = new Critter(drawableType, 1, 7, 20);			
		}
		if(drawableType == DrawableType.CATERPILLAR) {
			critter = new Critter(drawableType, 1, 7, 55);		
		}
		if(drawableType == DrawableType.CHIP_INFECTED) {
			critter = new Critter(drawableType, 1, 5, 20);		
		}
				
		critter.setX((int)(Math.random() * (Utils.getCanvasWidth() - critter.getWidth())));
		critter.setY((int)(Math.random() * (Utils.getCanvasHeight() - critter.getHeight())));
		adjustProperties();
		return critter;
	}	
	
	public void createPresetLevel(ArrayList<String> levelsData, ArrayList<Critter> critters) {
		Object level[] = levelsData.toArray();		
		
		for(int i = 0; i < level.length; i += 3) {
			int pos = i;
			if(level[pos].equals("spider")) {
				critter = new Critter(DrawableType.SPIDER, 1, 7, 50);					
			}
			else if(level[pos].equals("caterpillar")) {
				critter = new Critter(DrawableType.CATERPILLAR, 1, 7, 55);					
			}
			else if(level[pos].equals("chip")) {
				critter = new Critter(DrawableType.CHIP_INFECTED, 1, 5, 30);					
			}			
			
			critter.setX(Utils.convertXGridToWorld(Integer.parseInt(level[++pos].toString()) - 1, critter.getWidth()));			
			critter.setY(Utils.convertYGridToWorld(Integer.parseInt(level[++pos].toString()) - 1  + Utils.getGridYOffset(), critter.getHeight()));
			if(critter.getEnemyType() == CritterType.CATERPILLAR) {
				critter.setY(critter.getY() - (int)((critter.getHeight() / 1.5f) * 0.25f));
			}
			adjustProperties();			
			critters.add(critter);
		}
	}
	
	private void adjustProperties() {
		critter.setX(critter.getFixXPositionElement());
		if(critter.getEnemyType() != CritterType.CATERPILLAR) {
			critter.setY(critter.getFixYPositionElement());			
		}		
		critter.calcStartPositionGrid();		
		
		/*
		 * Fix position for resolution where Height/cellSize != int
		 */
		float fixYValue = Utils.getCanvasHeight()/(int)Utils.getCellSize();
		critter.setY(critter.getY() + fixYValue);		
		critter.setAdvanceDirection(0,1);
		critter.setSpeedToVerticalValue(GameRules.getCrittersStartSpeed(critter.getEnemyType()));
	}
	
	public static Critter createCritter(CritterType ct, int xGrid, int yGrid){
		Critter cr1;
		if(ct == CritterType.SPIDER)
		 cr1 = new Critter(DrawableType.SPIDER, 1, 7, 50);
		else// (ct == CritterType.CATERPILLAR)
			cr1 = new Critter(DrawableType.CATERPILLAR,  1, 7, 55);	
		cr1.setX(Utils.convertXGridToWorld(xGrid - 1, cr1.getWidth()));			
		cr1.setY(Utils.convertYGridToWorld(yGrid - 1  + Utils.getGridYOffset(), cr1.getHeight()));
		/*
		 * repeat code from critter factory 
		 */
		cr1.setX(cr1.getFixXPositionElement());
		if(cr1.getEnemyType() != CritterType.CATERPILLAR) {
			cr1.setY(cr1.getFixYPositionElement());			
		}		
		cr1.calcStartPositionGrid();		
		
		/*
		 * Fix position for resolution where Height/cellSize != int
		 */
		float fixYValue = Utils.getCanvasHeight()/(int)Utils.getCellSize();
		cr1.setY(cr1.getY() + fixYValue);		
		cr1.setAdvanceDirection(0,1);
		cr1.setSpeedToVerticalValue(GameRules.getCrittersStartSpeed(cr1.getEnemyType()));
		return cr1;
	}
}