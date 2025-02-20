package com.chesy.productiveslimes.network;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;

public class ModNetworkStateManager {
    private static final String KEY = "productiveslimes_cable_networks";

    public static ModNetworkState getOrCreate(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        ModNetworkState existing = manager.get(
                ModNetworkState::readNbt,
                KEY
        );

        if (existing == null) {
            existing = new ModNetworkState();
            manager.set(KEY, existing);
        }

        return existing;
    }

    public static void markDirty(ServerWorld world) {
        ModNetworkState state = getOrCreate(world);
        state.setDirty(true);
    }

    public static void forceSave(ServerWorld world) {
        world.getPersistentStateManager().save();
    }

    /**
     * Call this once (e.g. on server/world load) to re-register all stored networks
     * into ModNetworkManager’s in-memory map.
     */
    public static void loadAllNetworksToManager(ServerWorld world) {
        ModNetworkState state = getOrCreate(world);
        // Clear the manager's current map if you prefer a fresh load
        // ModNetworkManager.clear(); // <-- optionally clear your static map

        for (CableNetwork net : state.getAllNetworks().values()) {
            ModNetworkManager.addExistingNetwork(world, net);
        }
    }
}
