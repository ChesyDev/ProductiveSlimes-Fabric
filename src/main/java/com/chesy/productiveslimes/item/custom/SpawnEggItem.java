package com.chesy.productiveslimes.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;

public class SpawnEggItem extends net.minecraft.item.SpawnEggItem {
    private final int bg;
    private final int fg;

    public SpawnEggItem(EntityType<? extends MobEntity> type, int bg, int fg, Settings settings) {
        super(type, settings);
        this.bg = bg;
        this.fg = fg;
    }

    public int getBg(){
        return bg;
    }

    public int getFg() {
        return fg;
    }
}
