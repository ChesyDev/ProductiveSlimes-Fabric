package com.chesy.productiveslimes.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.world.World;

public class SlimySpider extends SpiderEntity {
    public SlimySpider(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
    }
}
