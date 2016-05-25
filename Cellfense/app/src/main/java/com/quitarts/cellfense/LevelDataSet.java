package com.quitarts.cellfense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LevelDataSet {
    private static int wave;
    private static ArrayList<String> singleLevel = new ArrayList<String>();
    private static LinkedHashMap<Integer, ArrayList<String>> levels = new LinkedHashMap<Integer, ArrayList<String>>();
    private static int levelResource;
    private static HashMap<Integer, Integer> resources = new HashMap<Integer, Integer>();
    private static ArrayList<String> levelTowers = new ArrayList<String>();
    private static HashMap<Integer, ArrayList<String>> towers = new HashMap<Integer, ArrayList<String>>();

    public static void addDataToLevel(String extractedValue) {
        singleLevel.add(extractedValue);
    }

    public static void setLevels() {
        levels.put(wave, (ArrayList<String>) singleLevel.clone());
        resources.put(wave, levelResource);
        towers.put(wave, (ArrayList<String>) levelTowers.clone());
        singleLevel.clear();
        levelTowers.clear();
    }

    public static LinkedHashMap<Integer, ArrayList<String>> getLevels() {
        return levels;
    }

    public static void reset() {
        singleLevel.clear();
        levels.clear();
        levelTowers.clear();
        towers.clear();
    }

    public static void setResources(int r) {
        levelResource = r;
    }

    public static HashMap<Integer, Integer> getResources() {
        return resources;
    }

    public static void setTowers(String t) {
        String[] splitTowers = t.split(",");
        for (String st : splitTowers) {
            levelTowers.add(st);
        }
    }

    public static HashMap<Integer, ArrayList<String>> getTowers() {
        return towers;
    }

    public static void setWave(int w) {
        wave = w;
    }
}