package com.chesy.productiveslimes.block.custom;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModButtonBlock extends ButtonBlock {
    public ModButtonBlock(BlockSetType blockSetType, int pressTicks, Settings settings, boolean wooden) {
        super(settings, blockSetType, pressTicks, wooden);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if (!entity.isOnGround() || entity.isSpectator() || entity.hasPassengers()) {
            return;
        }
        double slowFactor = 0.05;
        entity.setVelocity(
                entity.getVelocity().multiply(slowFactor, 1.0, slowFactor)
        );
    }
}
