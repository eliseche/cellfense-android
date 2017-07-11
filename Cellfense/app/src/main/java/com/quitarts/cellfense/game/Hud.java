package com.quitarts.cellfense.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.quitarts.cellfense.Utils;
import com.quitarts.cellfense.game.object.Button;
import com.quitarts.cellfense.game.object.Tower;
import com.quitarts.cellfense.game.object.base.GraphicObject;

import java.util.ArrayList;
import java.util.List;

public class Hud {
    private GameControl gameControl;
    private Rect hud;
    private Paint hudPaint;
    private Button buttonHudArrow;
    private Button buttonHudReadyEnabled;
    private ArrayList<String> towers;
    private Button buttonCapacitor;
    private Button buttonTank;
    private Button buttonBomb;
    private int slidePanelSpeed = 3;
    private Paint barsPaint;
    private Paint barsSeparatorPaint;
    private GraphicObject battery;
    private List<EnergyBar> energyBars = new ArrayList<>();

    public Hud(GameControl gameControl) {
        this.gameControl = gameControl;
        this.towers = LevelDataSet.getTowers().get(gameControl.getWave());

        initialize();
    }

    // region Update hud
    public void update(int dt) {
        updateResources(dt);
        updateHudArrow(dt);
        updateHudReady(dt);
    }

    private void updateResources(int dt) {
        energyBars.clear();
        int barWidth = (int) Utils.getCellWidth() * 10 / 100;
        int barHeight = (int) Utils.getCellHeight() * 30 / 100;
        int barSeparatorHeight = (int) Utils.getCellHeight() * 35 / 100;
        int separator = 0;
        int bars = gameControl.getResources() / 5;

        for (int i = 1; i <= bars; i++) {
            Rect energyBar = new Rect(battery.getGraphic().getBounds().left - separator,
                    battery.getGraphic().getBounds().top + 1,
                    battery.getGraphic().getBounds().left - separator - barWidth,
                    battery.getGraphic().getBounds().bottom / 2 + barHeight / 2);
            energyBars.add(new EnergyBar(energyBar, false));
            if (i % 5 == 0) {
                Rect energyBarSeparator = new Rect(energyBar.right - (int) (Utils.getCellWidth() * 8 / 100),
                        battery.getGraphic().getBounds().top,
                        energyBar.right - (int) (Utils.getCellWidth() * 5 / 100),
                        battery.getGraphic().getBounds().bottom / 2 + barSeparatorHeight / 2);
                separator += (int) (Utils.getCellWidth() * 23 / 100);
                energyBars.add(new EnergyBar(energyBarSeparator, true));
            } else {
                separator += (int) (Utils.getCellWidth() * 13 / 100);
            }
        }
    }

    private void updateHudArrow(int dt) {
        if (!(gameControl.getEnemyState() == GameControl.EnemyState.MOVING) && buttonHudArrow.getX() < 0)
            buttonHudArrow.setX(buttonHudArrow.getX() + 1 * (dt / slidePanelSpeed));
        else if (gameControl.getEnemyState() == GameControl.EnemyState.MOVING && buttonHudArrow.getX() + buttonHudArrow.getWidth() * 2 >= 0)
            buttonHudArrow.setX(buttonHudArrow.getX() - 1 * (dt / slidePanelSpeed));
        else if (gameControl.getEnemyState() == GameControl.EnemyState.MOVING && buttonHudArrow.getX() + buttonHudArrow.getX() < 0)
            return;
        else
            buttonHudArrow.setX(0);

        buttonHudArrow.setY(Utils.getCanvasHeight() - (buttonHudArrow.getHeight()));
    }

    private void updateHudReady(int dt) {
        if (!(gameControl.getEnemyState() == GameControl.EnemyState.MOVING) && buttonHudArrow.getX() < 0)
            buttonHudReadyEnabled.setX(buttonHudArrow.getGraphic().getBounds().right);
        else if (gameControl.getEnemyState() == GameControl.EnemyState.MOVING && buttonHudReadyEnabled.getX() + buttonHudReadyEnabled.getWidth() > 0)
            buttonHudReadyEnabled.setX(buttonHudReadyEnabled.getX() - 1 * (dt / slidePanelSpeed));
        else if (gameControl.getEnemyState() == GameControl.EnemyState.MOVING && buttonHudReadyEnabled.getX() + buttonHudReadyEnabled.getX() < 0)
            return;
        else
            buttonHudReadyEnabled.setX(buttonHudReadyEnabled.getWidth());

        buttonHudReadyEnabled.setY(Utils.getCanvasHeight() - (buttonHudReadyEnabled.getHeight()));
    }
    // endregion

    // region Draw hud
    public void drawBaseHud(Canvas canvas) {
        drawResources(canvas);
        drawHudArrow(canvas);
        drawHudReady(canvas);
    }

    public void drawResources(Canvas canvas) {
        battery.getGraphic().draw(canvas);

        for (EnergyBar energyBar : energyBars) {
            if (energyBar.isSeparator)
                canvas.drawRect(energyBar.getEnergyBar(), barsSeparatorPaint);
            else
                canvas.drawRect(energyBar.getEnergyBar(), barsPaint);
        }
    }

    public void drawBottomHud(Canvas canvas) {
        drawHud(canvas);

        for (String tower : towers) {
            if (tower.equals("tc"))
                drawButtonCapacitor(canvas);
            else if (tower.equals("tt"))
                drawButtonTank(canvas);
            else if (tower.equals("tb"))
                drawButtonBomb(canvas);
        }
    }

    private void drawHud(Canvas canvas) {
        canvas.drawRect(hud, hudPaint);
    }

    public void drawHudArrow(Canvas canvas) {
        buttonHudArrow.getGraphic().draw(canvas);
    }

    public void drawHudReady(Canvas canvas) {
        buttonHudReadyEnabled.getGraphic().draw(canvas);
    }
    // endregion

    // region Events
    public boolean buttonHudArrowClicked(int x, int y) {
        if (buttonHudArrow.isClicked(x, y))
            return true;

        return false;
    }

    public boolean hudTowerClick(int x, int y) {
        if (buttonCapacitor.isClicked(x, y)) {
            Tower towerCapacitor = new Tower(FactoryDrawable.DrawableType.GUN_TURRET_CAPACITOR_SPRITE, 1, 7, 30, false);
            towerCapacitor.setX(x);
            towerCapacitor.setY(y);
            gameControl.addTower(towerCapacitor);

            return true;
        } else if (buttonTank.isClicked(x, y)) {
            Tower towerTank = new Tower(FactoryDrawable.DrawableType.GUN_TURRET_TANK_SPRITE, 1, 7, 50, false);
            towerTank.setX(x);
            towerTank.setY(y);
            gameControl.addTower(towerTank);

            return true;
        } else if (buttonBomb.isClicked(x, y)) {
            Tower towerBomb = new Tower(FactoryDrawable.DrawableType.GUN_TURRET_BOMB_SPRITE, 1, 8, 150, true);
            gameControl.addTower(towerBomb);

            return true;
        }

        return false;
    }
    // endregion

    // region draw Towers
    private void drawButtonCapacitor(Canvas canvas) {
        buttonCapacitor.setX(canvas.getWidth() - buttonCapacitor.getWidth());
        buttonCapacitor.setY(canvas.getHeight() - buttonCapacitor.getHeight());
        buttonCapacitor.getGraphic().draw(canvas);
    }

    private void drawButtonTank(Canvas canvas) {
        buttonTank.setX(canvas.getWidth() - buttonTank.getWidth() * 2);
        buttonTank.setY(canvas.getHeight() - buttonTank.getHeight());
        buttonTank.getGraphic().draw(canvas);
    }

    private void drawButtonBomb(Canvas canvas) {
        buttonBomb.setX(canvas.getWidth() - buttonBomb.getWidth() * 3);
        buttonBomb.setY(canvas.getHeight() - buttonBomb.getHeight());
        buttonBomb.getGraphic().draw(canvas);
    }
    // endregion

    public int getTopBoundOfHud() {
        return Utils.getCanvasHeight() - hud.height();
    }

    public boolean nextWaveClicked(int x, int y) {
        if (buttonHudReadyEnabled.isClicked(x, y))
            return true;

        return false;
    }

    public void switchOnNextWaveButton() {
        buttonHudReadyEnabled.getGraphic().setAlpha(255);
    }

    public void switchOffNextWaveButton() {
        buttonHudReadyEnabled.getGraphic().setAlpha(130);
    }

    public boolean isHudAreaTouch(int y) {
        if (y > getTopBoundOfHud() + Utils.getCellHeight())
            return true;

        return false;
    }

    private void initialize() {
        // HudArrow
        buttonHudArrow = new Button(FactoryDrawable.DrawableType.HUD_ARROW);
        buttonHudArrow.setX(0);
        buttonHudArrow.setY(Utils.getCanvasHeight() - buttonHudArrow.getHeight());

        // HudReadyEnabled
        buttonHudReadyEnabled = new Button(FactoryDrawable.DrawableType.HUD_READY_ENABLED);
        buttonHudReadyEnabled.setX(buttonHudArrow.getWidth());
        buttonHudReadyEnabled.setY(Utils.getCanvasHeight() - buttonHudReadyEnabled.getHeight());

        // Hud
        hud = new Rect(0, (int) (Utils.getCanvasHeight() - Utils.getCellHeight() * 2), Utils.getCanvasWidth(), Utils.getCanvasHeight());
        hudPaint = new Paint();
        hudPaint.setStyle(Paint.Style.FILL);
        hudPaint.setARGB(180, 0, 0, 0);

        // Buttons towers
        buttonCapacitor = new Button(FactoryDrawable.DrawableType.GUN_TURRET_CAPACITOR);
        buttonTank = new Button(FactoryDrawable.DrawableType.GUN_TURRET_TANK);
        buttonBomb = new Button(FactoryDrawable.DrawableType.GUN_TURRET_BOMB);

        // EnergyBar
        barsPaint = new Paint();
        barsPaint.setStyle(Paint.Style.FILL);
        barsPaint.setARGB(255, 102, 102, 255);

        barsSeparatorPaint = new Paint();
        barsSeparatorPaint.setStyle(Paint.Style.FILL);
        barsSeparatorPaint.setARGB(255, 128, 141, 128);

        battery = new GraphicObject(FactoryDrawable.DrawableType.HUD_BATTERY);
        battery.setX(Utils.getCanvasWidth() - battery.getGraphic().getMinimumWidth());
        battery.setY(0);
    }

    class EnergyBar {
        private Rect energyBar;
        private boolean isSeparator;

        public EnergyBar(Rect energyBar, boolean isSeparator) {
            this.energyBar = energyBar;
            this.isSeparator = isSeparator;
        }

        public Rect getEnergyBar() {
            return energyBar;
        }

        public void setEnergyBar(Rect energyBar) {
            this.energyBar = energyBar;
        }

        public boolean isSeparator() {
            return isSeparator;
        }

        public void setSeparator(boolean separator) {
            isSeparator = separator;
        }
    }
}