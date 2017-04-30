package com.quitarts.cellfense.game.object;

import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.object.base.MovableTileAnimation;

public class Critter extends MovableTileAnimation {
    private CritterType type;

    public enum CritterType {
        SPIDER, CATERPILLAR, CHIP
    }

    public Critter(FactoryDrawable.DrawableType drawableType, int rows, int columns, int frameSkipDelay) {
        super(drawableType, rows, columns, frameSkipDelay, true);

        switch (drawableType) {
            case ENEMY_SPIDER_SPRITE:
                type = CritterType.SPIDER;
                break;
            case ENEMY_CATERPILLAR_SPRITE:
                type = CritterType.CATERPILLAR;
                break;
            case ENEMY_CHIP_INFECTED_SPRITE:
                type = CritterType.CHIP;
                break;
        }

        setDirection(0, 1);
    }

    public CritterType getEnemyType() {
        return type;
    }
}
