package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.handler.CableNetwork;
import com.chesy.productiveslimes.handler.NetworkManager;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CableBlockEntity extends BlockEntity implements EnergyStorage {
    public static final long CAPACITY_PER_CABLE = 10_000;
    private boolean initialized = false;
    public int energyStoredToLoad = -1;
    private boolean newlyPlaced = true;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE, pos, state);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            NetworkManager.onCableRemoved(serverWorld, pos);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, CableBlockEntity blockEntity) {
        if (!blockEntity.initialized) {
            blockEntity.initialized = true;
            if (!world.isClient && world instanceof ServerWorld serverWorld) {
                // Rebuild the network for this cable on load
                NetworkManager.rebuildNetwork(serverWorld, pos);
            }
        }
        else{
            blockEntity.newlyPlaced = false;
        }

        if (blockEntity.energyStoredToLoad >= 0){
            CableNetwork net = NetworkManager.getNetwork(pos);
            if (net != null){
                net.setTotalEnergy(blockEntity.energyStoredToLoad);
            }

            blockEntity.energyStoredToLoad = -1;
        }

        if (!world.isClient) {
            for (Direction direction : Direction.values()) {
                EnergyStorage neighbor = EnergyStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
                BlockEntity neighborBlockEntity = world.getBlockEntity(pos.offset(direction));

                if (neighborBlockEntity instanceof CableBlockEntity) {
                    continue;
                }

                if (neighbor != null) {
                    if (!neighbor.supportsInsertion()) continue;

                    try (Transaction transaction = Transaction.openOuter()) {
                        long amount = Math.min(blockEntity.getAmount() >= 1000 ? 1000 : blockEntity.getAmount(), neighbor.getCapacity() - neighbor.getAmount());

                        if (amount > 0) {
                            blockEntity.extract(amount,transaction);
                            neighbor.insert(amount,transaction);
                            transaction.commit();
                        }
                        else{
                            transaction.abort();
                        }
                    }
                }
            }
        }
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        CableNetwork net = NetworkManager.getNetwork(pos);
        return net == null ? 0 : net.insertEnergy(maxAmount);
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        CableNetwork net = NetworkManager.getNetwork(pos);
        return net == null ? 0 : net.extractEnergy(maxAmount);
    }

    @Override
    public long getAmount() {
        CableNetwork net = NetworkManager.getNetwork(pos);
        return net == null ? 0 : net.getTotalEnergy();
    }

    @Override
    public long getCapacity() {
        CableNetwork net = NetworkManager.getNetwork(pos);
        return net == null ? 0 : net.getTotalCapacity();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        long energyStored = getAmount();
        nbt.putLong("EnergyStored", energyStored == 0 ? -1 : energyStored);
        nbt.putBoolean("NewlyPlaced", newlyPlaced);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        if (nbt.contains("EnergyStored") && nbt.getLong("EnergyStored") != -1) {
            energyStoredToLoad = (int) nbt.getLong("EnergyStored");
        }
        newlyPlaced = nbt.getBoolean("NewlyPlaced");
    }
}
