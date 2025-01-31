package com.chesy.productiveslimes.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class CableNetwork {
    private int networkId = -1;
    private long totalEnergy = 0;
    private long totalCapacity = 0;

    private final Set<BlockPos> cablePositions = new HashSet<>();

    public CableNetwork() {}

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

    public static NbtCompound writeToNbt(CableNetwork net, NbtCompound nbt) {
        nbt.putInt("NetworkId", net.networkId);

        nbt.putLong("TotalEnergy", net.totalEnergy);
        nbt.putLong("TotalCapacity", net.totalCapacity);

        NbtList posList = new NbtList();
        for (BlockPos pos : net.cablePositions) {
            NbtCompound posTag = new NbtCompound();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            posList.add(posTag);
        }
        nbt.put("Positions", posList);

        return nbt;
    }

    public static CableNetwork readFromNbt(NbtCompound nbt) {
        CableNetwork net = new CableNetwork();

        if (nbt.contains("NetworkId")) {
            net.networkId = nbt.getInt("NetworkId");
        }

        net.totalEnergy = nbt.getLong("TotalEnergy");
        net.totalCapacity = nbt.getLong("TotalCapacity");

        if (nbt.contains("Positions", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Positions", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound posTag = list.getCompound(i);
                int x = posTag.getInt("x");
                int y = posTag.getInt("y");
                int z = posTag.getInt("z");
                net.cablePositions.add(new BlockPos(x, y, z));
            }
        }
        return net;
    }
}
