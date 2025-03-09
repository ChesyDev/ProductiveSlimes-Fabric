package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.network.CableNetwork;
import com.chesy.productiveslimes.network.ModNetworkManager;
import com.chesy.productiveslimes.util.IEnergyBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings({"deprecation", "null"})
public class CableBlockEntity extends BlockEntity implements EnergyStorage, IEnergyBlockEntity {
    public static final long CAPACITY_PER_CABLE = 10_000;
    private boolean initialized = false;
    private boolean newlyPlaced = true;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE, pos, state);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        assert world != null;
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            if (shouldRemoveCableEntity(serverWorld)) {
                ModNetworkManager.onCableRemoved(serverWorld, pos);
            }
        }
    }

    private boolean shouldRemoveCableEntity(ServerWorld serverWorld) {
        if (serverWorld.isChunkLoaded(this.pos)) {
            return !(serverWorld.getBlockEntity(pos) instanceof CableBlockEntity);
        }
        return false;
    }

    public static void tick(World world, BlockPos pos, CableBlockEntity blockEntity) {
        if (!blockEntity.initialized) {
            blockEntity.initialized = true;
            if (!world.isClient && world instanceof ServerWorld serverWorld && blockEntity.newlyPlaced) {
                ModNetworkManager.rebuildNetwork(serverWorld, pos);
            }
        }
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        CableNetwork net = ModNetworkManager.getNetwork(pos);
        return net == null ? 0 : net.insertEnergy(maxAmount);
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        CableNetwork net = ModNetworkManager.getNetwork(pos);
        return net == null ? 0 : net.extractEnergy(maxAmount);
    }

    @Override
    public long getAmount() {
        CableNetwork net = ModNetworkManager.getNetwork(pos);
        return net == null ? 0 : net.getTotalEnergy();
    }

    @Override
    public long getCapacity() {
        CableNetwork net = ModNetworkManager.getNetwork(pos);
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
        nbt.putBoolean("NewlyPlaced", newlyPlaced);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        newlyPlaced = nbt.getBoolean("NewlyPlaced", true);
    }

    @Override
    public CableBlockEntity getEnergyHandler() {
        return this;
    }
}
