package com.chesy.productiveslimes.network;

import com.chesy.productiveslimes.block.entity.CableBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class ModNetworkManager {
    private static final Map<BlockPos, CableNetwork> networkByPos = new HashMap<>();

    /**
     * Call this after placing a cable or somehow wanting to unify networks.
     */
    public static void rebuildNetwork(ServerWorld world, BlockPos startPos) {
        if (!(world.getBlockEntity(startPos) instanceof CableBlockEntity)) {
            return;
        }

        // We will BFS to find all cables connected to startPos
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos);

        // Keep track of old networks we are merging
        List<CableNetwork> oldNetworks = new ArrayList<>();
        long totalOldEnergy = 0;

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            if (!visited.add(currentPos)) continue;

            if (world.getBlockEntity(currentPos) instanceof CableBlockEntity) {
                CableNetwork oldNet = networkByPos.get(currentPos);
                if (oldNet != null && !oldNetworks.contains(oldNet)) {
                    oldNetworks.add(oldNet);
                }
                // Check neighbors
                for (Direction dir : Direction.values()) {
                    BlockPos neighborPos = currentPos.offset(dir);
                    if (world.getBlockEntity(neighborPos) instanceof CableBlockEntity) {
                        queue.add(neighborPos);
                    }
                }
            }
        }

        // Combine total energy from all old networks
        long totalOldCapacity = 0;
        for (CableNetwork oldNet : oldNetworks) {
            totalOldEnergy += oldNet.getTotalEnergy();
            totalOldCapacity += oldNet.getTotalCapacity();
        }

        // We'll remove the visited cables from old networks (since they are all about to merge).
        // If an old network becomes empty, we remove it from ModNetworkState as well.
        ModNetworkState state = ModNetworkStateManager.getOrCreate(world);

        for (CableNetwork oldNet : oldNetworks) {
            // Remove the BFS cables from this old network
            for (BlockPos cablePos : visited) {
                oldNet.removeCable(cablePos, CableBlockEntity.CAPACITY_PER_CABLE);
                networkByPos.remove(cablePos);
            }
            // If the old network is now empty, remove it from the state
            if (oldNet.getCablePositions().isEmpty()) {
                state.removeNetwork(oldNet.getNetworkId());
            }
        }

        // Create a brand new network in the state
        int newNetId = state.createNetwork();
        CableNetwork newNetwork = state.getNetwork(newNetId);

        // Add all BFS cables to the new network
        for (BlockPos cablePos : visited) {
            networkByPos.put(cablePos, newNetwork);
            newNetwork.addCable(cablePos, CableBlockEntity.CAPACITY_PER_CABLE);
        }

        // Merge the energy into the new network, respecting capacity
        long mergedEnergy = Math.min(totalOldEnergy, newNetwork.getTotalCapacity());
        newNetwork.setTotalEnergy(mergedEnergy);

        ModNetworkStateManager.markDirty(world);
    }

    /**
     * Call this after removing a cable at removedPos.
     * We then have to split the old network into sub-networks if applicable.
     */
    public static void onCableRemoved(ServerWorld world, BlockPos removedPos) {
        CableNetwork oldNet = networkByPos.get(removedPos);
        if (oldNet == null) {
            return;
        }

        long oldEnergy = oldNet.getTotalEnergy();
        oldNet.removeCable(removedPos, CableBlockEntity.CAPACITY_PER_CABLE);
        networkByPos.remove(removedPos);

        // If the old network is now empty, remove it entirely from the state
        ModNetworkState state = ModNetworkStateManager.getOrCreate(world);
        if (oldNet.getCablePositions().isEmpty()) {
            state.removeNetwork(oldNet.getNetworkId());
            ModNetworkStateManager.markDirty(world);
            return;
        }

        // Otherwise, we BFS to see if removing this cable has split the old network.
        // We find sub-networks among the oldNet's remaining cables.
        List<Set<BlockPos>> newSubnetworksCables = findSubnetworksAfterRemoval(world, oldNet);

        // If there's only one subnetwork that basically is the old network minus the removed cable,
        // you can just keep it as is. But if multiple sub-networks exist, we have to create new networks.
        if (newSubnetworksCables.isEmpty()) {
            // No cables left? Then we've already removed it from the state.
            return;
        }

        // Remove all cables from the old network (we'll reassign them to new sub-nets)
        Set<BlockPos> oldPositions = new HashSet<>(oldNet.getCablePositions());
        for (BlockPos pos : oldPositions) {
            oldNet.removeCable(pos, CableBlockEntity.CAPACITY_PER_CABLE);
            networkByPos.remove(pos);
        }
        // If oldNet is now empty, remove from state
        if (oldNet.getCablePositions().isEmpty()) {
            state.removeNetwork(oldNet.getNetworkId());
        }

        // Build new sub-networks in the state
        long sumCaps = 0;
        List<Long> subCaps = new ArrayList<>();
        for (Set<BlockPos> subSet : newSubnetworksCables) {
            long subCapacity = subSet.size() * CableBlockEntity.CAPACITY_PER_CABLE;
            subCaps.add(subCapacity);
            sumCaps += subCapacity;
        }

        // Distribute old energy proportionally
        long leftoverEnergy = oldEnergy;
        List<Long> subEnergies = new ArrayList<>(subCaps.size());
        for (long ci : subCaps) {
            double fraction = (double) ci / (double) sumCaps;
            long ei = (long) Math.floor(fraction * oldEnergy);
            subEnergies.add(ei);
            leftoverEnergy -= ei;
        }
        // Fix rounding
        if (leftoverEnergy > 0 && !subEnergies.isEmpty()) {
            int lastIndex = subEnergies.size() - 1;
            subEnergies.set(lastIndex, subEnergies.get(lastIndex) + leftoverEnergy);
        }

        // Create new networks for each sub-set
        for (int i = 0; i < newSubnetworksCables.size(); i++) {
            Set<BlockPos> subSet = newSubnetworksCables.get(i);
            long subCapacity = subCaps.get(i);
            long subEnergy = subEnergies.get(i);

            int newId = state.createNetwork();
            CableNetwork newNet = state.getNetwork(newId);

            for (BlockPos cablePos : subSet) {
                newNet.addCable(cablePos, CableBlockEntity.CAPACITY_PER_CABLE);
                networkByPos.put(cablePos, newNet);
            }
            newNet.setTotalEnergy(subEnergy);
        }

        ModNetworkStateManager.markDirty(world);
    }

    /**
     * BFS among oldNet's positions to see how many disconnected cable clusters exist.
     */
    private static List<Set<BlockPos>> findSubnetworksAfterRemoval(ServerWorld world, CableNetwork oldNet) {
        List<Set<BlockPos>> results = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();

        for (BlockPos start : oldNet.getCablePositions()) {
            if (!visited.contains(start)) {
                Set<BlockPos> subSet = new HashSet<>();
                Queue<BlockPos> queue = new LinkedList<>();
                queue.add(start);

                while (!queue.isEmpty()) {
                    BlockPos current = queue.poll();
                    if (!visited.add(current)) continue;

                    subSet.add(current);

                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = current.offset(dir);
                        if (oldNet.getCablePositions().contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
                }
                results.add(subSet);
            }
        }

        return results;
    }

    public static CableNetwork getNetwork(BlockPos pos) {
        return networkByPos.get(pos);
    }

    public static void addExistingNetwork(ServerWorld world, CableNetwork network) {
        for (BlockPos pos : network.getCablePositions()) {
            networkByPos.put(pos, network);
        }
    }
}
