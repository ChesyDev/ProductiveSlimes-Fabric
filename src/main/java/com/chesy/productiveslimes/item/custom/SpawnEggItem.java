package com.chesy.productiveslimes.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;

public class SpawnEggItem extends net.minecraft.item.SpawnEggItem {
    private final int color;

    public SpawnEggItem(EntityType<? extends MobEntity> type, int color, Settings settings) {
        super(settings.spawnEgg(type));
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
