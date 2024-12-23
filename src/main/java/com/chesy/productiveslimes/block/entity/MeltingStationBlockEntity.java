package com.chesy.productiveslimes.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MeltingStationBlockEntity extends BlockEntity {
    public MeltingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MELTING_STATION, pos, state);
    }
}
