package com.chesy.productiveslimes.handler;

import com.chesy.productiveslimes.block.entity.CableBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class NetworkManager {
    // A simple map storing all known cable positions -> the network they belong to
    private static final Map<BlockPos, CableNetwork> networkByPos = new HashMap<>();

    /**
     * Rebuild the network starting from `startPos`.
     * This method does a BFS to find all connected cables.
     */
    public static void rebuildNetwork(ServerWorld world, BlockPos startPos) {
        if (!(world.getBlockEntity(startPos) instanceof CableBlockEntity)) {
            return;
        }

        // BFS to find cables
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos);

        // Collect old networks to merge energy/capacity
        List<CableNetwork> oldNetworks = new ArrayList<>();
        long totalOldEnergy = 0;
        long totalOldCapacity = 0;

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            if (!visited.add(currentPos)) continue;

            if (world.getBlockEntity(currentPos) instanceof CableBlockEntity cableBE) {
                // Check old network
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

        // Sum up old networks’ energy + capacity
        for (CableNetwork oldNet : oldNetworks) {
            totalOldEnergy += oldNet.getTotalEnergy();
            totalOldCapacity += oldNet.getTotalCapacity();
        }

        // Create a new network
        CableNetwork newNetwork = new CableNetwork();
        // We'll set the initial totalEnergy after we add cables (to clamp properly).

        // For each visited cable, add it to the new network
        for (BlockPos cablePos : visited) {
            networkByPos.put(cablePos, newNetwork);
            newNetwork.addCable(cablePos, CableBlockEntity.CAPACITY_PER_CABLE);
        }

        // Now we have a brand-new network with capacity = #cables * CAPACITY_PER_CABLE
        // Let's incorporate the old energy, clamped to newNetwork’s capacity:
        long mergedEnergy = Math.min(totalOldEnergy, newNetwork.getTotalCapacity());
        newNetwork.setTotalEnergy(mergedEnergy);

        // Remove visited cables from old networks
        for (CableNetwork oldNet : oldNetworks) {
            for (BlockPos cablePos : visited) {
                oldNet.removeCable(cablePos, CableBlockEntity.CAPACITY_PER_CABLE);
            }
        }
    }

    /**
     * Whenever a cable is removed, we do a partial cleanup.
     * Then we might need to rebuild the networks for cables around that position.
     */
    public static void onCableRemoved(ServerWorld world, BlockPos removedPos) {
        // 1) Get the old network
        CableNetwork oldNet = networkByPos.get(removedPos);
        if (oldNet == null) {
            // If it’s not in any network, nothing to do
            return;
        }

        long oldEnergy = oldNet.getTotalEnergy();
        long oldCapacity = oldNet.getTotalCapacity();

        // 2) Remove the cable from the old network’s data structures
        //    (Adjust capacity by however much one cable contributed)
        oldNet.removeCable(removedPos, 10000);
        networkByPos.remove(removedPos);

        // Now, we suspect the old network might be “split up” because removing a cable
        // could disconnect parts of the network from each other.

        // 3) Collect the sets of cables forming each new subnetwork (via BFS from each neighbor).
        // We do NOT immediately assign them to a new CableNetwork. We first gather them all.
        List<Set<BlockPos>> newSubnetworksCables = findSubnetworksAfterRemoval(world, oldNet, removedPos);

        if (newSubnetworksCables.isEmpty()) {
            // Means that removing this cable left no other cables in oldNet
            // The old network is effectively destroyed.
            return;
        }

        // 4) Compute each subnetwork’s capacity
        List<Long> subCaps = new ArrayList<>();
        long sumCaps = 0;

        for (Set<BlockPos> subSet : newSubnetworksCables) {
            long subCapacity = subSet.size() * 10000;
            subCaps.add(subCapacity);
            sumCaps += subCapacity;
        }

        // 5) Distribute old energy among the new subnetworks proportionally
        //    E_i = E * (C_i / sumCaps)
        //    We must be mindful of integer rounding.
        //    For example, we can do floating or rational, or do a leftover pass.

        long leftoverEnergy = oldEnergy; // track how much we have left to distribute
        List<Long> subEnergies = new ArrayList<>(subCaps.size());

        for (int i = 0; i < subCaps.size(); i++) {
            long ci = subCaps.get(i);
            double fraction = (double) ci / (double) sumCaps;
            long ei = (long) Math.floor(fraction * oldEnergy);

            // Alternatively, use leftover distribution approach:
            //   ei = leftoverEnergy * ci / (sumCaps - distributedCapsSoFar)

            subEnergies.add(ei);
            leftoverEnergy -= ei;
        }

        // If there’s any rounding leftover, you can add it to one subnetwork or distribute it
        // arbitrarily. For example:
        if (leftoverEnergy > 0 && !subEnergies.isEmpty()) {
            // Just add leftover to the last subnetwork
            int lastIndex = subEnergies.size() - 1;
            subEnergies.set(lastIndex, subEnergies.get(lastIndex) + leftoverEnergy);
            leftoverEnergy = 0;
        }

        // 6) Now we create brand-new CableNetworks for each subnetwork and assign energies
        for (int i = 0; i < newSubnetworksCables.size(); i++) {
            Set<BlockPos> subSet = newSubnetworksCables.get(i);
            long subCap = subCaps.get(i);
            long subEnergy = subEnergies.get(i);

            CableNetwork newNet = new CableNetwork();
            // Instead of adding them one by one, we can do:
            for (BlockPos cablePos : subSet) {
                newNet.addCable(cablePos, 10000);
                networkByPos.put(cablePos, newNet);
            }
            // Set final energy, clamped just in case
            newNet.setTotalEnergy(subEnergy);
        }
    }

    /**
     * Finds all new subnetworks (connected sets of cables) after a cable is removed.
     * We have an oldNet that might now be disconnected. So we BFS from each neighbor
     * cable in oldNet that we haven't visited to get distinct connected components.
     */
    private static List<Set<BlockPos>> findSubnetworksAfterRemoval(
            ServerWorld world,
            CableNetwork oldNet,
            BlockPos removedPos
    ) {
        List<Set<BlockPos>> results = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();

        // We look at the cables that are still in the old network.
        // (We’ve already removed the cable at removedPos.)
        for (BlockPos cablePos : oldNet.getCablePositions()) {
            if (!visited.contains(cablePos)) {
                // BFS from this cable to get one connected component
                Set<BlockPos> subSet = new HashSet<>();
                Queue<BlockPos> queue = new LinkedList<>();
                queue.add(cablePos);

                while (!queue.isEmpty()) {
                    BlockPos current = queue.poll();
                    if (!visited.add(current)) continue;

                    // For each neighbor, if it's in oldNet, add it to BFS
                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = current.offset(dir);
                        // We check if neighbor is still in oldNet
                        // (We do not use networkByPos here because that might not be up to date
                        //  or might still reference oldNet. Instead, check oldNet's cablePositions directly)
                        if (oldNet.getCablePositions().contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }

                    subSet.add(current);
                }

                // We got one subnetwork
                results.add(subSet);
            }
        }

        return results;
    }


    /**
     * Returns the network for a given cable position, or null if none.
     */
    public static CableNetwork getNetwork(BlockPos pos) {
        return networkByPos.get(pos);
    }

    public static void addExistingNetwork(ServerWorld world, CableNetwork network) {
        // Add all positions from the loaded network to our networkByPos map
        for (BlockPos pos : network.getCablePositions()) {
            networkByPos.put(pos, network);
        }
    }

}