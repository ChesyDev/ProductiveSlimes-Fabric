package com.chesy.productiveslimes.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SolidingStationBlockEntity extends BlockEntity {
    public SolidingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLIDING_STATION, pos, state);
    }
}
