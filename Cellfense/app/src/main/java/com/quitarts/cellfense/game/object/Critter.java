package com.quitarts.cellfense.game.object;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.FactoryDrawable;
import com.quitarts.cellfense.game.object.base.MovableTileAnimation;
import com.quitarts.pathfinder.Path;

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
    private Path critterPath1 = new Path();
    private Path critterPath2 = new Path();
    private Path critterPath3 = new Path();
    private Path critterPath4 = new Path();

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
        Map<Path, Integer> paths = new HashMap<>();
        paths.put(critterPath1, critterPath1.getLength());
        paths.put(critterPath2, critterPath2.getLength());
        paths.put(critterPath3, critterPath3.getLength());
        paths.put(critterPath4, critterPath4.getLength());

        List<Map.Entry<Path, Integer>> pathsList = new ArrayList<>(paths.entrySet());
        Collections.sort(pathsList, new Comparator<LinkedHashMap.Entry<Path, Integer>>() {
            @Override
            public int compare(LinkedHashMap.Entry<Path, Integer> path1, LinkedHashMap.Entry<Path, Integer> path2) {
                return path1.getValue().compareTo(path2.getValue());
            }
        });

        return pathsList.get(0).getKey();
    }

    public void setCritterPath(String pathName, Path path) {
        switch (pathName) {
            case "PATH1":
                int value = 0;
                while (value < path.getLength()) {
                    critterPath1.appendStep(path.getStep(value).getX(), path.getStep(value).getY());
                    value++;
                }
                value = 0;
                while ((value < critterPath1.getLength())) {
                    critterPath1.getStep(value).setX(Utils.convertXGridToWorld(critterPath1.getStep(value).getX()));
                    critterPath1.getStep(value).setY(Utils.convertYGridToWorld(critterPath1.getStep(value).getY()));
                    value++;
                }

                break;
            case "PATH2":
                value = 0;
                while (value < path.getLength()) {
                    critterPath2.appendStep(path.getStep(value).getX(), path.getStep(value).getY());
                    value++;
                }
                value = 0;
                while ((value < critterPath2.getLength())) {
                    critterPath2.getStep(value).setX(Utils.convertXGridToWorld(critterPath2.getStep(value).getX()));
                    critterPath2.getStep(value).setY(Utils.convertYGridToWorld(critterPath2.getStep(value).getY()));
                    value++;
                }

                break;
            case "PATH3":
                value = 0;
                while (value < path.getLength()) {
                    critterPath3.appendStep(path.getStep(value).getX(), path.getStep(value).getY());
                    value++;
                }
                value = 0;
                while ((value < critterPath3.getLength())) {
                    critterPath3.getStep(value).setX(Utils.convertXGridToWorld(critterPath3.getStep(value).getX()));
                    critterPath3.getStep(value).setY(Utils.convertYGridToWorld(critterPath3.getStep(value).getY()));
                    value++;
                }

                break;
            case "PATH4":
                value = 0;
                while (value < path.getLength()) {
                    critterPath4.appendStep(path.getStep(value).getX(), path.getStep(value).getY());
                    value++;
                }
                value = 0;
                while ((value < critterPath4.getLength())) {
                    critterPath4.getStep(value).setX(Utils.convertXGridToWorld(critterPath4.getStep(value).getX()));
                    critterPath4.getStep(value).setY(Utils.convertYGridToWorld(critterPath4.getStep(value).getY()));
                    value++;
                }

                break;
        }
    }
}
