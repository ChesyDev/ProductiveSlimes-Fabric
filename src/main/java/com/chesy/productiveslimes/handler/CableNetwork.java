package com.chesy.productiveslimes.handler;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CableNetwork {
    private long totalEnergy = 0;
    private long totalCapacity = 0;

    private final Set<BlockPos> cablePositions = new HashSet<>();

    public CableNetwork() {}

    public void addCable(BlockPos pos, long cableCapacity) {
        if (cablePositions.add(pos)) {
            totalCapacity += 10000;
            if (totalEnergy > totalCapacity) {
                totalEnergy = totalCapacity;
            }
        }
    }

    public void removeCable(BlockPos pos, long cableCapacity) {
        if (cablePositions.remove(pos)) {
            totalCapacity -= 10000;
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
        // Make sure not to exceed totalCapacity
        this.totalEnergy = Math.min(newAmount, totalCapacity);
    }

    /**
     * Insert energy up to the network’s total capacity.
     */
    public long insertEnergy(long amount) {
        long space = totalCapacity - totalEnergy;
        long accepted = Math.min(space, amount);
        totalEnergy += accepted;
        return accepted;
    }

    /**
     * Extract energy from the network.
     */
    public long extractEnergy(long amount) {
        long extracted = Math.min(totalEnergy, amount);
        totalEnergy -= extracted;
        return extracted;
    }

    public static void writeToNbt(CableNetwork net, NbtCompound nbt) {
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
    }

    public static CableNetwork readFromNbt(NbtCompound nbt) {
        CableNetwork net = new CableNetwork();
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
