package com.chesy.productiveslimes.network;

import com.chesy.productiveslimes.block.entity.CableBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class ModNetworkManager {
    private static final Map<BlockPos, CableNetwork> networkByPos = new HashMap<>();

    public static void rebuildNetwork(ServerWorld world, BlockPos startPos) {
        if (!(world.getBlockEntity(startPos) instanceof CableBlockEntity)) {
            return;
        }

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos);

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

        for (CableNetwork oldNet : oldNetworks) {
            totalOldEnergy += oldNet.getTotalEnergy();
            totalOldCapacity += oldNet.getTotalCapacity();
        }

        CableNetwork newNetwork = new CableNetwork();

        for (BlockPos cablePos : visited) {
            networkByPos.put(cablePos, newNetwork);
            newNetwork.addCable(cablePos, CableBlockEntity.CAPACITY_PER_CABLE);
        }

        long mergedEnergy = Math.min(totalOldEnergy, newNetwork.getTotalCapacity());
        newNetwork.setTotalEnergy(mergedEnergy);

        for (CableNetwork oldNet : oldNetworks) {
            for (BlockPos cablePos : visited) {
                oldNet.removeCable(cablePos, CableBlockEntity.CAPACITY_PER_CABLE);
            }
        }
    }

    public static void onCableRemoved(ServerWorld world, BlockPos removedPos) {
        CableNetwork oldNet = networkByPos.get(removedPos);
        if (oldNet == null) {
            return;
        }

        long oldEnergy = oldNet.getTotalEnergy();
        long oldCapacity = oldNet.getTotalCapacity();

        oldNet.removeCable(removedPos, 10000);
        networkByPos.remove(removedPos);

        List<Set<BlockPos>> newSubnetworksCables = findSubnetworksAfterRemoval(world, oldNet, removedPos);

        if (newSubnetworksCables.isEmpty()) {
            return;
        }

        List<Long> subCaps = new ArrayList<>();
        long sumCaps = 0;

        for (Set<BlockPos> subSet : newSubnetworksCables) {
            long subCapacity = subSet.size() * 10000;
            subCaps.add(subCapacity);
            sumCaps += subCapacity;
        }

        long leftoverEnergy = oldEnergy;
        List<Long> subEnergies = new ArrayList<>(subCaps.size());

        for (int i = 0; i < subCaps.size(); i++) {
            long ci = subCaps.get(i);
            double fraction = (double) ci / (double) sumCaps;
            long ei = (long) Math.floor(fraction * oldEnergy);

            subEnergies.add(ei);
            leftoverEnergy -= ei;
        }

        if (leftoverEnergy > 0 && !subEnergies.isEmpty()) {
            int lastIndex = subEnergies.size() - 1;
            subEnergies.set(lastIndex, subEnergies.get(lastIndex) + leftoverEnergy);
            leftoverEnergy = 0;
        }

        for (int i = 0; i < newSubnetworksCables.size(); i++) {
            Set<BlockPos> subSet = newSubnetworksCables.get(i);
            long subCap = subCaps.get(i);
            long subEnergy = subEnergies.get(i);

            CableNetwork newNet = new CableNetwork();
            for (BlockPos cablePos : subSet) {
                newNet.addCable(cablePos, 10000);
                networkByPos.put(cablePos, newNet);
            }
            newNet.setTotalEnergy(subEnergy);
        }
    }

    private static List<Set<BlockPos>> findSubnetworksAfterRemoval(
            ServerWorld world,
            CableNetwork oldNet,
            BlockPos removedPos
    ) {
        List<Set<BlockPos>> results = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();

        for (BlockPos cablePos : oldNet.getCablePositions()) {
            if (!visited.contains(cablePos)) {
                Set<BlockPos> subSet = new HashSet<>();
                Queue<BlockPos> queue = new LinkedList<>();
                queue.add(cablePos);

                while (!queue.isEmpty()) {
                    BlockPos current = queue.poll();
                    if (!visited.add(current)) continue;

                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = current.offset(dir);
                        if (oldNet.getCablePositions().contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }

                    subSet.add(current);
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