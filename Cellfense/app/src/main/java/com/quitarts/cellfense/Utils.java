package com.quitarts.cellfense;

import java.util.LinkedHashMap;

public class Utils {
    public static final int GAMEMAP_WIDTH = 8;
    public static final int GAMEMAP_HEIGHT = 12;
    private static int width;
    private static int height;
    private static float cellWidth;
    private static float cellHeight;
    private static int offsetY;
    private static final LinkedHashMap<Integer, Integer> levelUnlock = new LinkedHashMap<>();
    public static final String UNLOCKED_ART = "unlocked";

    public static void setCanvasSize(int width, int height) {
        Utils.width = width;
        Utils.height = height;
    }

    public static void setCellSize(float cellWidth, float cellHeight) {
        Utils.cellWidth = cellWidth;
        Utils.cellHeight = cellHeight;
    }

    public static int getCanvasWidth() {
        return Utils.width;
    }

    public static int getCanvasHeight() {
        return Utils.height;
    }

    public static float getCellWidth() {
        return Utils.cellWidth;
    }

    public static float getCellHeight() {
        return Utils.cellHeight;
    }

    // Main game loop will be running at 25 FPS
    public static int getFramesPerSecond() {
        return 25;
    }

    public static void setLevelUnlock() {
        Utils.levelUnlock.put(2, 2);
        Utils.levelUnlock.put(5, 5);
        Utils.levelUnlock.put(9, 8);
        Utils.levelUnlock.put(12, 11);
        Utils.levelUnlock.put(17, 14);
        Utils.levelUnlock.put(21, 17);
        Utils.levelUnlock.put(26, 20);
    }

    public static int getLevelUnlock(int level) {
        if (Utils.levelUnlock.containsKey(level))
            return Utils.levelUnlock.get(level);

        return 0;
    }

    public static int getOffsetY() {
        return offsetY;
    }

    public static void setOffsetY(int offsetY) {
        Utils.offsetY = offsetY;
    }

    public static int convertXGridToWorld(int x) {
        return (int) (cellWidth * x);
    }

    public static int convertYGridToWorld(int y) {
        return (int) (cellHeight * y);
    }

    public static int convertXWorldToGrid(float x) {
        return (int) (x / cellWidth);
    }

    public static int convertYWorldToGrid(float y) {
        return (int) (y / cellHeight);
    }

    public static float getScaleFactor() {
        return ContextContainer.getContext().getResources().getDisplayMetrics().density;
    }
}