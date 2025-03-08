package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.util.FluidStack;
import com.chesy.productiveslimes.util.IFluidBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity extends BlockEntity implements IFluidBlockEntity {
    private final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET * 50, this::update);

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TANK, pos, state);
    }

    private void update() {
        markDirty();
        if(world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public SingleFluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public SingleVariantStorage<FluidVariant> getFluidHandler() {
        return fluidStorage;
    }

    public void setFluidVariant(FluidVariant stack) {
        fluidStorage.variant = stack;
    }

    public FluidVariant getFluidVariant() {
        return fluidStorage.getResource();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        var fluidNbt = new NbtCompound();
        this.fluidStorage.writeNbt(fluidNbt, registries);
        nbt.put("FluidTank", fluidNbt);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        if(nbt.contains("FluidTank", NbtElement.COMPOUND_TYPE)) {
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registries);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        var nbt = super.toInitialChunkDataNbt(registries);
        writeNbt(nbt, registries);
        return nbt;
    }

    public void tick(World level, BlockPos blockPos, BlockState blockState){
        Direction direction = Direction.DOWN;
        BlockPos neighborPos = this.getPos().offset(direction);
        Storage<FluidVariant> neighborStorage = FluidStorage.SIDED.find(world, neighborPos, direction.getOpposite());

        if (neighborStorage != null) {
            try(Transaction transaction = Transaction.openOuter()){
                FluidStack availableFluid = new FluidStack(fluidStorage.getResource().getFluid(), fluidStorage.getAmount());

                if (availableFluid.isEmpty()) {
                    return;
                }

                long neighborFluid = neighborStorage.insert(availableFluid.copy().getFluid(), Math.min(FluidConstants.BUCKET, availableFluid.getAmount()), transaction);
                if (!availableFluid.isEmpty() && neighborFluid > 0) {
                    long drained = fluidStorage.extract(availableFluid.getFluid(), neighborFluid, transaction);
                    if (drained > 0) {
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
