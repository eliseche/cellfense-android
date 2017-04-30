package com.quitarts.cellfense.game;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Critter;
import com.quitarts.cellfense.game.object.Tower;

public class GameRules {
    public static float getDamageEnemy(Tower tower, Critter critter) {
        switch (tower.getType()) {
            case TURRET_CAPACITOR:
                if (critter.getEnemyType() == Critter.CritterType.SPIDER)
                    return 13f;
                else if (critter.getEnemyType() == Critter.CritterType.CATERPILLAR)
                    return 4.15f;
                else if (critter.getEnemyType() == Critter.CritterType.CHIP)
                    return 5f;

                break;
            case TURRET_TANK:
                if (critter.getEnemyType() == Critter.CritterType.SPIDER)
                    return 16.666f;
                else if (critter.getEnemyType() == Critter.CritterType.CATERPILLAR)
                    return 20f;
                else if (critter.getEnemyType() == Critter.CritterType.CHIP)
                    return 5f;

                break;
            case TURRET_BOMB:
                if (critter.getEnemyType() == Critter.CritterType.SPIDER)
                    return 100f;
                else if (critter.getEnemyType() == Critter.CritterType.CATERPILLAR)
                    return 100f;
                else if (critter.getEnemyType() == Critter.CritterType.CHIP)
                    return 100f;
        }

        return 0f;
    }

    public static int getTowerShootingTime(Tower.TowerType type) {
        switch (type) {
            case TURRET_CAPACITOR:
                return 500;
            case TURRET_TANK:
                return 1300;
        }

        return 0;
    }

    public static float getTowerShootingRange(Tower.TowerType type) {
        switch (type) {
            case TURRET_CAPACITOR:
                return Utils.getCellSize() * 1.8f;
            case TURRET_TANK:
                return Utils.getCellSize() * 1.8f;
            case TURRET_BOMB:
                return Utils.getCellSize() * 1.8f;
        }

        return 0f;
    }

    public static int getTowerPrice(Tower.TowerType type) {
        switch (type) {
            case TURRET_CAPACITOR:
                return 25;
            case TURRET_TANK:
                return 25;
            case TURRET_BOMB:
                return 50;
        }

        return 0;
    }

    public static int getTowerCrazyPrice() {
        return 10;
    }

    public static float getCritterSpeed(Critter.CritterType type) {
        switch (type) {
            case SPIDER:
                return 1.4f;
            case CATERPILLAR:
                return 1.0f;
            case CHIP:
                return 3.0f;
        }

        return 0;
    }

    public static int getCritterSlowTime() {
        return 3000;
    }

    public static int getStartLives() {
        return 1;
    }

    public static int getMaxTowers() {
        return 10;
    }

    public static int getLTAPrice() {
        return 5;
    }
}
