package com.chesy.productiveslimes.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;

public class SlimyZombie extends ZombieEntity {
    public SlimyZombie(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }
}
