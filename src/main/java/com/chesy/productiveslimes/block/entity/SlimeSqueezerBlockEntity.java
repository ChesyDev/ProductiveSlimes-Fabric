package com.chesy.productiveslimes.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SlimeSqueezerBlockEntity extends BlockEntity {
    public SlimeSqueezerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SLIME_SQUEEZER, pos, state);
    }
}
