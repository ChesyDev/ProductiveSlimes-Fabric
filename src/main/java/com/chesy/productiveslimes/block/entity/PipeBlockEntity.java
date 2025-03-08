package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.network.pipe.ModPipeNetworkManager;
import com.chesy.productiveslimes.network.pipe.PipeNetwork;
import com.chesy.productiveslimes.fluid.FluidStack;
import com.chesy.productiveslimes.util.IFluidBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
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

public class PipeBlockEntity extends BlockEntity implements IFluidBlockEntity {
    public static final long CAPACITY_PER_CABLE = FluidConstants.BUCKET;
    private boolean initialized = false;
    private boolean newlyPlaced = true;

    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        public long insert(FluidVariant o, long l, TransactionContext transactionContext) {
            PipeNetwork net = ModPipeNetworkManager.getNetwork(getPos());
            return net != null ? net.insertFluid(new FluidStack(o.getFluid(), l), false) : 0;
        }

        @Override
        public long extract(FluidVariant o, long l, TransactionContext transactionContext) {
            PipeNetwork net = ModPipeNetworkManager.getNetwork(getPos());
            return net != null ? net.extractFluid(new FluidStack(o.getFluid(), l), false) : 0;
        }

        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant fluidVariant) {
            PipeNetwork net = ModPipeNetworkManager.getNetwork(getPos());
            return net != null ? net.getTotalCapacity() : 0;
        }

        @Override
        public FluidVariant getResource() {
            PipeNetwork net = ModPipeNetworkManager.getNetwork(getPos());
            return net != null ? net.getFluidStack().getFluid() : FluidVariant.blank();
        }

        @Override
        public long getAmount() {
            PipeNetwork net = ModPipeNetworkManager.getNetwork(getPos());
            return net != null ? net.getTotalFluid() : 0;
        }
    };

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE, pos, state);
    }

    @Override
    public SingleVariantStorage<FluidVariant> getFluidHandler() {
        return fluidStorage;
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if(!world.isClient && world instanceof ServerWorld serverLevel) {
            if (shouldRemoveCableEntity(serverLevel)) {
                ModPipeNetworkManager.onCableRemoved(serverLevel, this.getPos());
            }
        }
    }

    private boolean shouldRemoveCableEntity(ServerWorld serverWorld) {
        if (serverWorld.isChunkLoaded(this.getPos())) {
            return !(serverWorld.getBlockEntity(this.getPos()) instanceof PipeBlockEntity);
        }
        return false;
    }

    public static void tick(World level, BlockPos pos, PipeBlockEntity blockEntity) {
        if (!blockEntity.initialized) {
            blockEntity.initialized = true;
            if (!level.isClient && level instanceof ServerWorld serverWorld && blockEntity.newlyPlaced) {
                ModPipeNetworkManager.rebuildNetwork(serverWorld, pos);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putBoolean("NewlyPlaced", newlyPlaced);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        newlyPlaced = nbt.getBoolean("NewlyPlaced");
    }
}
