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
        cablePositions.add(pos);
        // Increase the network’s total capacity by this cable’s capacity
        this.totalCapacity += cableCapacity;
    }

    public void removeCable(BlockPos pos, long cableCapacity) {
        cablePositions.remove(pos);
        // Decrease total capacity
        this.totalCapacity -= cableCapacity;
        // Clamp to avoid going negative in edge cases
        if (this.totalCapacity < 0) {
            this.totalCapacity = 0;
        }
        // If totalEnergy now exceeds totalCapacity (e.g., removing cables from a big network), clamp it:
        if (this.totalEnergy > this.totalCapacity) {
            this.totalEnergy = this.totalCapacity;
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

    // NBT Save method
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("totalEnergy", totalEnergy);
        nbt.putLong("totalCapacity", totalCapacity);
        int i = 0;
        // Save cable positions
        NbtList positionsList = new NbtList();
        for (BlockPos pos : cablePositions) {
            NbtCompound posCompound = new NbtCompound();
            NbtHelper.toBlockPos(posCompound, "pos" + i);
            positionsList.add(posCompound);
            i++;
        }
        nbt.put("cablePositions", positionsList);

        return nbt;
    }

    // NBT Load method
    public void readNbt(NbtCompound nbt) {
        totalEnergy = nbt.getLong("totalEnergy");
        totalCapacity = nbt.getLong("totalCapacity");

        // Load cable positions
        cablePositions.clear();
        NbtList positionsList = nbt.getList("cablePositions", 10); // 10 is the ID for compound tags
        for (int i = 0; i < positionsList.size(); i++) {
            NbtCompound posCompound = positionsList.getCompound(i);
            Optional<BlockPos> pos = NbtHelper.toBlockPos(posCompound, "pos" + i);
            if (!pos.isPresent()) {
                continue;
            }
            cablePositions.add(pos.get());
        }
    }
}
