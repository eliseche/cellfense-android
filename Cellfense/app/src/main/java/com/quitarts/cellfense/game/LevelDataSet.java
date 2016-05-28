package com.quitarts.cellfense.game;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LevelDataSet {
    private static int wave;
    // Vars per level
    private static ArrayList<String> level = new ArrayList<>();
    private static int levelResource;
    private static ArrayList<String> levelTowers = new ArrayList<>();
    // Vars for use in game (all data)
    private static LinkedHashMap<Integer, ArrayList<String>> levels = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Integer> resources = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, ArrayList<String>> towers = new LinkedHashMap<>();

    public static void setWave(int wave) {
        LevelDataSet.wave = wave;
    }

    public static void setLevel(String level) {
        LevelDataSet.level.add(level);
    }

    public static void setLevelResource(int resource) {
        LevelDataSet.levelResource = resource;
    }

    public static void setLevelTowers(String towers) {
        String[] splitTowers = towers.split(",");
        for (String tower : splitTowers) {
            LevelDataSet.levelTowers.add(tower);
        }
    }

    public static void setLevels() {
        LevelDataSet.levels.put(wave, (ArrayList<String>) level.clone());
        LevelDataSet.resources.put(wave, levelResource);
        LevelDataSet.towers.put(wave, (ArrayList<String>) levelTowers.clone());

        LevelDataSet.level.clear();
        LevelDataSet.levelResource = 0;
        LevelDataSet.levelTowers.clear();
    }

    public static LinkedHashMap<Integer, ArrayList<String>> getLevels() {
        return levels;
    }

    public static LinkedHashMap<Integer, Integer> getResources() {
        return resources;
    }

    public static LinkedHashMap<Integer, ArrayList<String>> getTowers() {
        return towers;
    }

    public static void reset() {
        LevelDataSet.level.clear();
        LevelDataSet.levelResource = 0;
        LevelDataSet.levelTowers.clear();

        LevelDataSet.levels.clear();
        LevelDataSet.resources.clear();
        LevelDataSet.towers.clear();
    }
}