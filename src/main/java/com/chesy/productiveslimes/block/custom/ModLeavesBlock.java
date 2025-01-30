package com.chesy.productiveslimes.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.MangroveLeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ModLeavesBlock extends LeavesBlock {
    public final MapCodec<MangroveLeavesBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codecs.rangedInclusiveFloat(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter((mangroveLeavesBlock) -> super.leafParticleChance), createSettingsCodec()).apply(instance, MangroveLeavesBlock::new));

    public ModLeavesBlock(float leafParticleChance, Settings settings) {
        super(leafParticleChance, settings);
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

    @Override
    public MapCodec<? extends LeavesBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void spawnLeafParticle(World world, BlockPos pos, Random random) {

    }
}
