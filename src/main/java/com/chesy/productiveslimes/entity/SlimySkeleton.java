package com.chesy.productiveslimes.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.world.World;

public class SlimySkeleton extends SkeletonEntity {
    public SlimySkeleton(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }
}
