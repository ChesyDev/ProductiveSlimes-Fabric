package com.chesy.productiveslimes.handler;

import com.chesy.productiveslimes.block.entity.CableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class EnergyNetwork implements EnergyStorage {
    private final Set<CableBlockEntity> cables = new HashSet<>();
    private CableBlockEntity primaryCable;
    private long energyStored = 0;
    private long maxEnergyStored = 0;
    // Add a cable to the network
    public void addCable(CableBlockEntity cable) {
        if (cables.add(cable)) {
            maxEnergyStored += cable.getCapacity();
            cable.setNetwork(this);
            if (primaryCable == null || cable.getPos().compareTo(primaryCable.getPos()) < 0) {
                primaryCable = cable;
            }
        }
    }
    // Get the primary cable
    public CableBlockEntity getPrimaryCable() {
        return primaryCable;
    }
    // Update the primary cable if necessary
    private void updatePrimaryCable() {
        primaryCable = cables.stream()
                .min(Comparator.comparing(BlockEntity::getPos))
                .orElse(null);
    }
    // Remove a cable from the network
    public void removeCable(CableBlockEntity cable) {
        if (cables.remove(cable)) {
            maxEnergyStored -= cable.getCapacity();
            energyStored = Math.min(energyStored, maxEnergyStored);
            if (cables.isEmpty()) {
                // Invalidate the network
                invalidate();
            } else {
                if (cable.equals(primaryCable)) {
                    updatePrimaryCable();
                }
                // Check for network splits
                splitNetwork(cable);
            }
            cable.setNetwork(null);
        }
    }
    private void splitNetwork(CableBlockEntity removedCable) {
        // Create new networks starting from the neighboring cables
        List<Set<CableBlockEntity>> subNetworks = new ArrayList<>();
        Set<CableBlockEntity> visited = new HashSet<>();
        for (CableBlockEntity cable : cables) {
            if (!visited.contains(cable)) {
                Set<CableBlockEntity> subNetworkCables = new HashSet<>();
                exploreNetwork(cable, subNetworkCables, visited);
                subNetworks.add(subNetworkCables);
            }
        }
        if (subNetworks.size() > 1) {
            // Split the energy among the new networks
            int totalCables = cables.size() + 1; // +1 for the removed cable
            long energyPerCable = energyStored / totalCables;
            for (Set<CableBlockEntity> subNetworkCables : subNetworks) {
                EnergyNetwork newNetwork = new EnergyNetwork();
                long newNetworkCapacity = 0;
                for (CableBlockEntity cable : subNetworkCables) {
                    newNetwork.addCable(cable);
                    cable.setNetwork(newNetwork);
                    newNetworkCapacity += cable.getCapacity();
                }
                newNetwork.setEnergyStored(energyPerCable * subNetworkCables.size());
            }
            // Invalidate the old network
            invalidate();
        }
    }
    private void exploreNetwork(CableBlockEntity cable, Set<CableBlockEntity> subNetworkCables, Set<CableBlockEntity> visited) {
        visited.add(cable);
        subNetworkCables.add(cable);
        for (Direction direction : Direction.values()) {
            BlockEntity neighborBE = cable.getWorld().getBlockEntity(cable.getPos().offset(direction));
            if (neighborBE instanceof CableBlockEntity neighborCable) {
                if (cables.contains(neighborCable) && !visited.contains(neighborCable)) {
                    exploreNetwork(neighborCable, subNetworkCables, visited);
                }
            }
        }
    }

    @Override
    public long insert(long l, TransactionContext transactionContext) {
        return 0;
    }

    @Override
    public long extract(long l, TransactionContext transactionContext) {
        return 0;
    }

    @Override
    public long getAmount() {
        return 0;
    }

    public long getEnergyStored() {
        return energyStored;
    }

    public long getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public long getCapacity() {
        return 0;
    }

    public void setEnergyStored(long energyStored) {
        this.energyStored = energyStored;
    }

    public void merge(EnergyNetwork other) {
        if (other == this) return;
        energyStored += other.energyStored;
        maxEnergyStored += other.maxEnergyStored;
        for (CableBlockEntity cable : other.cables) {
            cables.add(cable);
            cable.setNetwork(this);
        }
        other.invalidate();
    }

    private void invalidate() {
        cables.clear();
        energyStored = 0;
        maxEnergyStored = 0;
    }

    public void distributeEnergy(World world) {
        for (CableBlockEntity cable : cables) {
            BlockPos pos = cable.getPos();
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                EnergyStorage neighborEnergy = EnergyStorage.SIDED.find(world, neighborPos, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy != this && neighborEnergy.supportsInsertion()) {
                    long energyAvailable = this.extract(cable.getTransferRate(), Transaction.openOuter());
                    long energyReceived = neighborEnergy.insert(energyAvailable, Transaction.openOuter());
                    long transferAmount = Math.min(energyAvailable, energyReceived);
                    if (transferAmount > 0) {
                        this.extract(transferAmount, Transaction.openOuter());
                        neighborEnergy.insert(transferAmount, Transaction.openOuter());
                    }
                }
            }
        }
    }
    public void collectEnergy(World world) {
        for (CableBlockEntity cable : cables) {
            BlockPos pos = cable.getPos();
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                EnergyStorage neighborEnergy = EnergyStorage.SIDED.find(world, neighborPos, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy != this && neighborEnergy.supportsExtraction()) {
                    long energyNeeded = this.insert(cable.getTransferRate(), Transaction.openOuter());
                    long energyExtracted = neighborEnergy.extract(energyNeeded, Transaction.openOuter());
                    long transferAmount = Math.min(energyNeeded, energyExtracted);
                    if (transferAmount > 0) {
                        neighborEnergy.extract(transferAmount, Transaction.openOuter());
                        this.insert(transferAmount, Transaction.openOuter());
                    }
                }
            }
        }
    }
}
