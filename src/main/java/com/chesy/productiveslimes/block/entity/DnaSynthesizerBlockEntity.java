package com.chesy.productiveslimes.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class DnaSynthesizerBlockEntity extends BlockEntity {
    public DnaSynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DNA_SYNTHESIZER, pos, state);
    }
}
