package com.quitarts.cellfense.game;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to get BitmapDrawable based on DrawableType
 */
public class FactoryDrawable {
    private static Map<DrawableType, BitmapDrawable> bitmapDrawables = new HashMap<>();

    public static enum DrawableType {
        // Gun Capacitor
        GUN_TURRET_CAPACITOR, GUN_TURRET_CAPACITOR_SPRITE,
        // Gun Tank
        GUN_TURRET_TANK, GUN_TURRET_TANK_BASE, GUN_TURRET_TANK_SPRITE,
        // Gun Bomb
        GUN_TURRET_BOMB, GUN_TURRET_BOMB_CRATER, GUN_TURRET_BOMB_SPRITE,
        // Enemies
        ENEMY_SPIDER_SPRITE, ENEMY_CATERPILLAR_SPRITE, ENEMY_CHIP_INFECTED_SPRITE,
        // LTA (A.K.A Fireball)
        GUN_LTA_POWER_SPRITE, GUN_LTA_FIRE_SPRITE,
        // Hud
        HUD, HUD_ARROW, HUD_READY, HUD_READY_PUSHED, HUD_BATTERY,
        // Tutorial
        TUTORIAL_VS, TUTORIAL_FINGER
    }

    public static BitmapDrawable createDrawable(DrawableType typeToBuild) {
        BitmapDrawable bitmapDrawable = bitmapDrawables.get(typeToBuild);

        if (bitmapDrawable == null) {
            switch (typeToBuild) {
                case GUN_TURRET_CAPACITOR:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_capacitor);
                    break;
                case GUN_TURRET_CAPACITOR_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_capacitor_sprite);
                    break;
                case GUN_TURRET_TANK:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_tank);
                    break;
                case GUN_TURRET_TANK_BASE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_tank_base);
                    break;
                case GUN_TURRET_TANK_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_tank_sprite);
                    break;
                case GUN_TURRET_BOMB:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_bomb);
                    break;
                case GUN_TURRET_BOMB_CRATER:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_bomb_crater);
                    break;
                case GUN_TURRET_BOMB_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_turret_bomb_sprite);
                    break;
                case ENEMY_SPIDER_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.enemy_spider_sprite);
                    break;
                case ENEMY_CATERPILLAR_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.enemy_caterpillar_sprite);
                    break;
                case ENEMY_CHIP_INFECTED_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.enemy_chip_infected);
                    break;
                case GUN_LTA_POWER_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_lta_power_sprite);
                    break;
                case GUN_LTA_FIRE_SPRITE:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.gun_lta_fire_sprite);
                    break;
                case HUD:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.hud);
                    break;
                case HUD_ARROW:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.hud_arrow);
                    break;
                case HUD_READY:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.hud_ready);
                    break;
                case HUD_READY_PUSHED:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.hud_ready_pushed);
                    break;
                case HUD_BATTERY:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.hud_battery);
                    break;
                case TUTORIAL_VS:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.tutorial_vs);
                    break;
                case TUTORIAL_FINGER:
                    bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(ContextContainer.getContext(), R.drawable.tutorial_finger);
                    break;
                default:
                    break;
            }

            bitmapDrawables.put(typeToBuild, bitmapDrawable);
        }

        return bitmapDrawable;
    }
}