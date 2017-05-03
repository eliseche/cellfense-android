package com.quitarts.cellfense.game;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Critter;

import java.util.ArrayList;

/**
 * Created by alejandro on 25/04/17.
 */

public class CritterFactory {
    ArrayList<Critter> critters;
    Critter critter;

    public ArrayList<Critter> createPresetLevel(ArrayList<String> levelsData) {
        critters = new ArrayList<>();
        Object level[] = levelsData.toArray();

        for (int i = 0; i < level.length; i += 3) {
            int pos = i;
            if (level[pos].equals("spider")) {
                critter = new Critter(FactoryDrawable.DrawableType.ENEMY_SPIDER_SPRITE, 1, 7, 50);
            } else if (level[pos].equals("caterpillar")) {
                critter = new Critter(FactoryDrawable.DrawableType.ENEMY_CATERPILLAR_SPRITE, 1, 7, 55);
            } else if (level[pos].equals("chip")) {
                critter = new Critter(FactoryDrawable.DrawableType.ENEMY_CHIP_INFECTED_SPRITE, 1, 5, 30);
            }

            critter.setX(Utils.convertXGridToWorld(Integer.parseInt(level[++pos].toString()) - 1));
            critter.setY(Utils.convertYGridToWorld(Integer.parseInt(level[++pos].toString()) - 1));
            critter.setDirection(0, 1);
            critter.setSpeedY(GameRules.getCritterSpeed(critter.getEnemyType()) * Utils.getCellHeight());

            critters.add(critter);
        }

        return critters;
    }
}
