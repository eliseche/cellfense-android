package com.quitarts.cellfense.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.quitarts.cellfense.ContextContainer;
import com.quitarts.cellfense.R;
import com.quitarts.cellfense.Utils;

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
        HUD, HUD_ARROW, HUD_READY_ENABLED, HUD_READY_DISABLED, HUD_BATTERY,
        // Tutorial
        TUTORIAL_VS, TUTORIAL_FINGER
    }

    public static BitmapDrawable createDrawable(DrawableType typeToBuild) {
        Bitmap bitmap = null;
        BitmapDrawable bitmapDrawable = bitmapDrawables.get(typeToBuild);

        if (bitmapDrawable == null) {
            switch (typeToBuild) {
                case GUN_TURRET_CAPACITOR:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_capacitor);
                    break;
                case GUN_TURRET_CAPACITOR_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_capacitor_sprite);
                    break;
                case GUN_TURRET_TANK:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_tank);
                    break;
                case GUN_TURRET_TANK_BASE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_tank_base);
                    break;
                case GUN_TURRET_TANK_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_tank_sprite);
                    break;
                case GUN_TURRET_BOMB:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_bomb);
                    break;
                case GUN_TURRET_BOMB_CRATER:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_bomb_crater);
                    break;
                case GUN_TURRET_BOMB_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_turret_bomb_sprite);
                    break;
                case ENEMY_SPIDER_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.enemy_spider_sprite);
                    break;
                case ENEMY_CATERPILLAR_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.enemy_caterpillar_sprite);
                    break;
                case ENEMY_CHIP_INFECTED_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.enemy_chip_infected_sprite);
                    break;
                case GUN_LTA_POWER_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_lta_power_sprite);
                    break;
                case GUN_LTA_FIRE_SPRITE:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.gun_lta_fire_sprite);
                    break;
                case HUD:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.hud);
                    break;
                case HUD_ARROW:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.hud_arrow);
                    break;
                case HUD_READY_ENABLED:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.hud_ready_enabled);
                    break;
                case HUD_READY_DISABLED:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.hud_ready_disabled);
                    break;
                case HUD_BATTERY:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.hud_battery);
                    break;
                case TUTORIAL_VS:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.tutorial_vs);
                    break;
                case TUTORIAL_FINGER:
                    bitmap = BitmapFactory.decodeResource(ContextContainer.getContext().getResources(), R.drawable.tutorial_finger);
                    break;
                default:
                    break;
            }

            switch (typeToBuild) {
                case GUN_TURRET_CAPACITOR:
                case GUN_TURRET_TANK:
                case GUN_TURRET_TANK_BASE:
                case GUN_TURRET_BOMB:
                case GUN_TURRET_BOMB_CRATER:
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) Utils.getCellWidth(), (int) Utils.getCellHeight(), false);
                    break;
                case GUN_TURRET_CAPACITOR_SPRITE:
                case GUN_TURRET_TANK_SPRITE:
                case ENEMY_SPIDER_SPRITE:
                case ENEMY_CATERPILLAR_SPRITE:
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) Utils.getCellWidth() * 7, (int) Utils.getCellHeight(), false);
                    break;
                case GUN_TURRET_BOMB_SPRITE:
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) Utils.getCellWidth() * 8, (int) Utils.getCellHeight(), false);
                    break;
                case ENEMY_CHIP_INFECTED_SPRITE:
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) Utils.getCellWidth() * 5, (int) Utils.getCellHeight(), false);
                    break;
                case GUN_LTA_POWER_SPRITE:
                case GUN_LTA_FIRE_SPRITE:
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) Utils.getCellWidth() * 15, (int) Utils.getCellHeight(), false);
                    break;
                default:
                    break;
            }

            bitmapDrawable = new BitmapDrawable(ContextContainer.getContext().getResources(), bitmap);
            bitmapDrawables.put(typeToBuild, bitmapDrawable);
        }

        return bitmapDrawable;
    }
}