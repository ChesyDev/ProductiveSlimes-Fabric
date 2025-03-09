package com.chesy.productiveslimes.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CableNetwork {
    public static final Codec<BlockPos> BLOCK_POS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(BlockPos::getX),
            Codec.INT.fieldOf("y").forGetter(BlockPos::getY),
            Codec.INT.fieldOf("z").forGetter(BlockPos::getZ)
    ).apply(instance, BlockPos::new));

    public static final Codec<CableNetwork> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("NetworkId", -1).forGetter(net -> net.networkId),
            Codec.LONG.fieldOf("TotalEnergy").forGetter(net -> net.totalEnergy),
            Codec.LONG.fieldOf("TotalCapacity").forGetter(net -> net.totalCapacity),
            BLOCK_POS_CODEC.listOf().fieldOf("Positions").forGetter(net -> net.cablePositions.stream().toList())
    ).apply(instance, CableNetwork::new));

    private int networkId = -1;
    private long totalEnergy = 0;
    private long totalCapacity = 0;

    private final Set<BlockPos> cablePositions = new HashSet<>();

    public CableNetwork() {}

    private CableNetwork(int networkId, long totalEnergy, long totalCapacity, List<BlockPos> cablePositions) {
        this.networkId = networkId;
        this.totalEnergy = totalEnergy;
        this.totalCapacity = totalCapacity;
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
            if (totalEnergy > totalCapacity) {
                totalEnergy = totalCapacity;
            }
        }
    }

    public void removeCable(BlockPos pos, long cableCapacity) {
        if (cablePositions.remove(pos)) {
            totalCapacity -= cableCapacity;
            if (totalCapacity < 0) totalCapacity = 0;
            if (totalEnergy > totalCapacity) totalEnergy = totalCapacity;
        }
    }

    public Set<BlockPos> getCablePositions() {
        return cablePositions;
    }

    public long getTotalEnergy() {
        return totalEnergy;
    }

    public long getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalEnergy(long newAmount) {
        this.totalEnergy = Math.min(newAmount, totalCapacity);
    }

    public long insertEnergy(long amount) {
        long space = totalCapacity - totalEnergy;
        long accepted = Math.min(space, amount);
        totalEnergy += accepted;
        return accepted;
    }

    public long extractEnergy(long amount) {
        long extracted = Math.min(totalEnergy, amount);
        totalEnergy -= extracted;
        return extracted;
    }
}
