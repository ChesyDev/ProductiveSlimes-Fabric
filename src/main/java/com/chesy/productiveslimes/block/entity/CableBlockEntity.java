package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.handler.EnergyNetwork;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.HashSet;
import java.util.Set;

public class CableBlockEntity extends BlockEntity implements EnergyStorage {
    private long energyStoredToLoad = -1;
    private EnergyNetwork network;
    private final long capacity = 10000; // Example capacity
    private final long transferRate = 500; // Energy transfer rate per tick

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE, pos, state);
    }

    public void setNetwork(EnergyNetwork network) {
        this.network = network;
    }
    public EnergyNetwork getNetwork() {
        return network;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }
    public long getTransferRate() {
        return transferRate;
    }

    @Override
    public long insert(long maxInsert, TransactionContext transactionContext) {
        return network != null ? network.insert(maxInsert, transactionContext) : 0;
    }

    @Override
    public long extract(long maxExtract, TransactionContext transactionContext) {
        return network != null ? network.extract(maxExtract, transactionContext) : 0;
    }

    @Override
    public long getAmount() {
        return network != null ? network.getAmount() : 0;
    }

    public long getEnergyStored() {
        return network != null ? network.getEnergyStored() : 0;
    }

    public long getMaxEnergyStored() {
        return network != null ? network.getMaxEnergyStored() : capacity;
    }

    @Override
    public boolean supportsExtraction() {
        return network != null && network.supportsExtraction();
    }

    @Override
    public boolean supportsInsertion() {
        return network != null && network.supportsInsertion();
    }

    private boolean isPrimaryCable() {
        // For example, the cable with the lowest position
        return network != null && this.equals(network.getPrimaryCable());
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (!world.isClient && network != null) {
            network.removeCable(this);
            network = null;
        }
    }

    private void initializeNetwork() {
        if (network != null) {
            return; // Already initialized
        }
        Set<EnergyNetwork> adjacentNetworks = new HashSet<>();
        for (Direction direction : Direction.values()) {
            BlockEntity neighborBE = world.getBlockEntity(pos.offset(direction));
            if (neighborBE instanceof CableBlockEntity neighborCable) {
                if (neighborCable.network != null) {
                    adjacentNetworks.add(neighborCable.network);
                }
            }
        }
        if (adjacentNetworks.isEmpty()) {
            network = new EnergyNetwork();
            network.addCable(this);
        } else {
            // Merge all adjacent networks
            network = adjacentNetworks.iterator().next();
            network.addCable(this);
            for (EnergyNetwork adjNetwork : adjacentNetworks) {
                if (adjNetwork != network) {
                    network.merge(adjNetwork);
                }
            }
        }
    }
    public static void tick(World world, BlockPos pos, BlockState state, CableBlockEntity cable) {
        if (!world.isClient && cable.network != null) {
            cable.network.collectEnergy(world);
            cable.network.distributeEnergy(world);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        long energyStored = network != null ? network.getEnergyStored() : 0;
        nbt.putLong("EnergyStored", energyStored);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        energyStoredToLoad = nbt.getLong("EnergyStored");
    }

    public void onRemoved() {
        if (network != null) {
            network.removeCable(this);
            network = null;
        }
    }
    public void reinitializeNetwork() {
        if (!world.isClient) {
            if (network != null) {
                network.removeCable(this);
                network = null;
            }
            initializeNetwork();
        }
    }
}
