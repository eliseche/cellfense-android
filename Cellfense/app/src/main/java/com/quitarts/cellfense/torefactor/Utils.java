package com.quitarts.cellfense.torefactor;

import com.quitarts.cellfense.ContextContainer;

import java.util.Hashtable;


public class Utils {	
    private static int width;
    private static int height;
    private static float cellSize;
    private static int offsetY;

    public static final int DIALOG_GAMEOVER_ID = 0;
    public static final int DIALOG_PLAYAGAIN_ID = 1;
    public static final int DIALOG_LOADING_OFF_ID = 2;
    public static final int DIALOG_BACK_BUTTON_PRESS_ID = 3;
    public static final int DIALOG_BACK_BUTTON_CONFIRM_ID = 4;    
    public static final int DIALOG_LEVEL_PASS_ID = 5;
    public static final int ACTIVITY_LEVELS_ID = 6;
    public static final int FROM_LEVEL_SELECTION_ID = 7;
    public static final int POST_SCORE_ID = 8;
    public static final int TUTORIAL_S5_L1_POPUP_WIN = 9;
    public static final int TUTORIAL_S11_L2_POPUP_LOSE = 10;
    public static final int TUTORIAL_S11_L2_POPUP_WIN = 11;
    public static final int TUTORIAL_S19_L3_POPUP_LOSE = 12;
    public static final int TUTORIAL_S19_L3_POPUP_WIN = 13;
    public static final int TUTORIAL_S26_L4_POPUP_LOSE = 14;
    public static final int TUTORIAL_S26_L4_POPUP_WIN = 15;
    public static final int TUTORIAL_END = 16;
    public static final int NEW_UNLOCKED_ART = 17;
    
    public static final String UNLOCKED_ART = "unlocked";

	public static final Hashtable<String, Integer>  LevelUnlock = new Hashtable<String, Integer>();
    
    public static void setCanvasSize(int width, int height) {
    	Utils.width = width;
        Utils.height = height;        
    }
    

    
    public static void setCellSize(float cs){
    	cellSize = cs;
    }
    
    public static float getCellSize(){
    	return cellSize;
    }
    
    public static int getCanvasWidth() {
    	return width;
    }
    
    public static int getCanvasHeight() {
    	return height;
    }
    
    public static int getFramePerSecondControlValue(){
    	return 25;
    }
    

    
    public static float getScaleFactor() {
    	return ContextContainer.getContext().getResources().getDisplayMetrics().density;
    }
    
    public static int convertXGridToWorld(int x, int widthOfObject) {
    	float tileWidth = getCanvasWidth() / GameMap.WIDTH;
    	return (int)(tileWidth * x);
    }
    
    public static int convertYGridToWorld(int y, int heightObject) {
    	float tileHeight = Utils.getCellSize();
    	return (int)(tileHeight * y);
    }
    
    public static int convertXWorldToGrid(float x, int widthOfObject) {
    	float tileWidth = getCanvasWidth() / GameMap.WIDTH;
    	return (int)(x / tileWidth);
    }
    
    public static int convertYWorldToGrid(float y, int heightObject) {
    	float tileHeight = Utils.getCellSize();
    	return Math.round(y / tileHeight);
    }
    
    public static int getGridYOffset(){
    	return (int)((height - cellSize * 12)/cellSize);
    }
    
    public static void setOffsetY(int offSet){
    	offsetY = offSet;
    }
    
    public static int getOffsetY(){
    	return offsetY;
    }
    
    public static int GetEnergyBarSize(){
    	/*
    	 * 5% of the cellSize
    	 */
    	return (int)cellSize*5/100;
    }
    
    public static void setLevelUnlockedValues(){
    	LevelUnlock.put(String.valueOf(2), 2);
    	LevelUnlock.put(String.valueOf(5), 5);
    	LevelUnlock.put(String.valueOf(9), 8);
    	LevelUnlock.put(String.valueOf(12), 11);
    	LevelUnlock.put(String.valueOf(17), 14);
    	LevelUnlock.put(String.valueOf(21), 17);
    	LevelUnlock.put(String.valueOf(26), 20);
    }
    
    public static int getLevelUnlocked(int level){
    	Integer value = (Integer)LevelUnlock.get(String.valueOf(level));
    	if(value != null)
    		return value;
    	return 0;
    }
}