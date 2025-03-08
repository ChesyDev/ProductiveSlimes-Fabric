package com.chesy.productiveslimes.network.pipe;

import com.chesy.productiveslimes.fluid.FluidStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PipeNetwork {
    public static final Codec<BlockPos> BLOCK_POS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(BlockPos::getX),
            Codec.INT.fieldOf("y").forGetter(BlockPos::getY),
            Codec.INT.fieldOf("z").forGetter(BlockPos::getZ)
    ).apply(instance, BlockPos::new));

    public static final Codec<PipeNetwork> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("NetworkId", -1).forGetter(net -> net.networkId),
            Codec.LONG.fieldOf("TotalFluid").forGetter(net -> net.totalFluid),
            Codec.STRING.fieldOf("Fluid").forGetter(net -> Registries.FLUID.getId(net.fluid).toString()),
            Codec.LONG.fieldOf("TotalCapacity").forGetter(net -> net.totalCapacity),
            BLOCK_POS_CODEC.listOf().fieldOf("Positions").forGetter(net -> net.cablePositions.stream().toList())
    ).apply(instance, PipeNetwork::new));

    private int networkId = -1;
    private Fluid fluid = Fluids.EMPTY;
    private long totalFluid = 0;
    private long totalCapacity = 0;

    private final Set<BlockPos> cablePositions = new HashSet<>();

    public PipeNetwork() {}

    private PipeNetwork(int networkId, long totalFluid, String fluid, long totalCapacity, List<BlockPos> cablePositions) {
        this.networkId = networkId;
        this.totalFluid = totalFluid;
        this.totalCapacity = totalCapacity;
        this.fluid = Registries.FLUID.get(Identifier.tryParse(fluid));
        this.cablePositions.addAll(cablePositions);
    }

    // NEW: Accessors for the network ID
    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int id) {
        this.networkId = id;
    }

    public void addCable(BlockPos pos, long cableCapacity) {
        if (cablePositions.add(pos)) {
            totalCapacity += cableCapacity;
            if (totalFluid > totalCapacity) {
                totalFluid = totalCapacity;
            }
        }
    }

    public void removeCable(BlockPos pos, long cableCapacity) {
        if (cablePositions.remove(pos)) {
            totalCapacity -= cableCapacity;
            if (totalCapacity < 0) totalCapacity = 0;
            if (totalFluid > totalCapacity) totalFluid = totalCapacity;
        }
    }

    public Set<BlockPos> getCablePositions() {
        return cablePositions;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public long getTotalFluid() {
        return totalFluid;
    }

    public FluidStack getFluidStack(){
        return new FluidStack(getFluid(), getTotalFluid());
    }

    public long getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalFluid(long totalFluid) {
        this.totalFluid = totalFluid;
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }

    public long insertFluid(FluidStack fluidStack, boolean simulate) {
        if (this.fluid.matchesType(Fluids.EMPTY)) {
            long accepted = Math.min(fluidStack.getAmount(), this.totalCapacity);
            if (!simulate) {
                this.fluid = fluidStack.getFluid().getFluid();
                this.totalFluid = accepted;
            }
            return accepted;
        }
        long space = totalCapacity - totalFluid;
        long accepted = Math.min(space, fluidStack.getAmount());
        if (!simulate) {
            totalFluid += accepted;
        }
        return accepted;
    }

    public long extractFluid(FluidStack fluidStack, boolean simulate) {
        if (fluidStack.isEmpty()) {
            return 0;
        }
        return drain(fluidStack.getAmount(), simulate);
    }

    public long drain(long maxDrain, boolean simulate) {
        long drained = Math.min(maxDrain, this.totalFluid);
        Fluid fluid = this.fluid;
        if (drained <= 0) {
            return 0;
        }
        if (!simulate) {
            this.totalFluid -= drained;
            if (this.totalFluid == 0) {
                this.fluid = Fluids.EMPTY;
            }
        }
        return drained;
    }
}