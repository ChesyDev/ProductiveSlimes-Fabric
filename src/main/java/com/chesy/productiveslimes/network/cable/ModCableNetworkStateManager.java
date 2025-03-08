package com.chesy.productiveslimes.network.cable;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;

public class ModCableNetworkStateManager {
    private static final String KEY = "productiveslimes_cable_networks";

    public static ModCableNetworkState getOrCreate(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        ModCableNetworkState existing = manager.get(ModCableNetworkState.MY_TYPE);

        if (existing == null) {
            existing = new ModCableNetworkState();
            manager.set(ModCableNetworkState.MY_TYPE, existing);
        }

        return existing;
    }

    public static void markDirty(ServerWorld world) {
        ModCableNetworkState state = getOrCreate(world);
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
        ModCableNetworkState state = getOrCreate(world);
        // Clear the manager's current map if you prefer a fresh load
        // ModNetworkManager.clear(); // <-- optionally clear your static map

        for (CableNetwork net : state.getAllNetworks().values()) {
            ModCableNetworkManager.addExistingNetwork(world, net);
        }
    }
}