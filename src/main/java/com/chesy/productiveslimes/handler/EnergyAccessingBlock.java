package com.chesy.productiveslimes.handler;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

import java.util.Optional;

public class EnergyAccessingBlock {
    private final World world;

    public EnergyAccessingBlock(World world) {
        this.world = world;
    }

    public Optional<EnergyStorage> getNeighborEnergyStorage(BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.offset(direction);

        BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

        if(neighborPos instanceof EnergyStorage energyStorage) {
            return Optional.of(energyStorage);
        }

        return Optional.empty();
    }
}
