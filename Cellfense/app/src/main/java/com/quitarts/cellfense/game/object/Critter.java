package com.quitarts.cellfense.game.object;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.GameMap;
import com.quitarts.cellfense.game.UnitMover;
import com.quitarts.cellfense.game.object.base.MovableTileAnimation;
import com.quitarts.pathfinder.AStarPathFinder;
import com.quitarts.pathfinder.Path;
import com.quitarts.pathfinder.PathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Critter extends MovableTileAnimation {
    private CritterType type;
    private int indexNextStep = 0;
    private Map<Path, Integer> paths = new HashMap<>();

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

    public int getXFix() {
        return ((int) getX() / getWidth()) * getWidth();
    }

    public int getYFix() {
        return ((int) getY() / getHeight()) * getHeight();
    }

    public void advance(int dt) {
        super.advance(dt);

        if (indexNextStep < getShortestPath().getLength()) {
            Path.Step indexActualStep = getShortestPath().getStep(indexNextStep);

            if (getY() > (Utils.getCanvasHeight() + indexActualStep.getY() - Utils.getCellHeight()) && indexNextStep == 0) {
                setY(indexActualStep.getY() + Utils.getOffsetY() - Utils.getCellHeight());
                indexNextStep++;
                decideDirection(indexNextStep, indexNextStep - 1);
                setRotationAngle(0);
                return;
            }

            if (indexNextStep > 0) {
                if (getDirection()[0] == -1) {
                    if (getX() < indexActualStep.getX()) {
                        setX(indexActualStep.getX());
                        indexNextStep++;
                        decideDirection(indexNextStep, indexNextStep + 1);
                    }
                    setRotationAngle(90);
                } else if (this.getDirection()[0] == 1) {
                    if (getX() >= indexActualStep.getX()) {
                        setX(indexActualStep.getX());
                        indexNextStep++;
                        decideDirection(indexNextStep, indexNextStep + 1);
                    }
                    setRotationAngle(-90);
                } else if (this.getDirection()[1] == -1) {
                    if (getY() - Utils.getOffsetY() + Utils.getCellHeight() < indexActualStep.getY()) {
                        setY(indexActualStep.getY() + Utils.getOffsetY() - Utils.getCellHeight());
                        indexNextStep++;
                        decideDirection(indexNextStep, indexNextStep + 1);
                    }
                    setRotationAngle(180);
                } else if (this.getDirection()[1] == 1) {
                    if (getY() - Utils.getOffsetY() + Utils.getCellHeight() >= indexActualStep.getY()) {
                        setY(indexActualStep.getY() + Utils.getOffsetY() - Utils.getCellHeight());
                        indexNextStep++;
                        decideDirection(indexNextStep, indexNextStep + 1);
                    }
                    setRotationAngle(0);
                }

                decideDirection(indexNextStep, indexNextStep - 1);
            }
        }
    }

    private void decideDirection(int indexActualStep, int indexLastStep) {
        if (indexActualStep >= getShortestPath().getLength()) {
            setDirection(0, 1);
            setRotationAngle(0);
        } else if (indexLastStep >= getShortestPath().getLength()) {
            setDirection(0, 1);
            setRotationAngle(0);
        } else {
            Path.Step stepActual = getShortestPath().getStep(indexActualStep);
            Path.Step stepLast = getShortestPath().getStep(indexLastStep);

            if (stepActual.getY() == stepLast.getY()) {
                if (stepActual.getX() <= stepLast.getX()) {
                    setDirection(-1, 0);
                    return;
                } else if (stepActual.getX() > stepLast.getX()) {
                    setDirection(1, 0);
                    return;
                }
            } else {
                if (stepActual.getY() <= stepLast.getY()) {
                    setDirection(0, -1);
                    return;
                } else if (stepActual.getY() > stepLast.getY()) {
                    setDirection(0, 1);
                    return;
                }
            }

            setDirection(0, 1);
            setRotationAngle(0);
        }
    }

    private Path getShortestPath() {
        List<Map.Entry<Path, Integer>> pathsList = new ArrayList<>(paths.entrySet());
        Collections.sort(pathsList, new Comparator<LinkedHashMap.Entry<Path, Integer>>() {
            @Override
            public int compare(LinkedHashMap.Entry<Path, Integer> path1, LinkedHashMap.Entry<Path, Integer> path2) {
                return path1.getValue().compareTo(path2.getValue());
            }
        });

        return pathsList.get(0).getKey();
    }

    public void setCritterPaths(GameMap gameMap) {
        PathFinder finder = new AStarPathFinder(gameMap, 500, false);

        // calculate path for x from 0-7
        for (int position = 0; position < 8; position++) {
            Path path = finder.findPath(new UnitMover(0), Utils.convertXWorldToGrid(getX()), 0, position, Utils.GAMEMAP_HEIGHT - 1);
            // Convert units from gameMap to gameWorld
            int pathIndex = 0;
            while (pathIndex < path.getLength()) {
                Path.Step step = path.getStep(pathIndex);
                step.setX(Utils.convertXGridToWorld(step.getX()));
                step.setY(Utils.convertYGridToWorld(step.getY()));
                pathIndex++;
            }

            paths.put(path, path.getLength());
        }
    }
}
