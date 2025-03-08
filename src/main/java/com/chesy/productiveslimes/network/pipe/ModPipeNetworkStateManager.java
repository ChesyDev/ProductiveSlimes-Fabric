package com.chesy.productiveslimes.network.pipe;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;

public class ModPipeNetworkStateManager {
    private static final String KEY = "productiveslimes_pipe_networks";

    public static ModPipeNetworkState getOrCreate(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        ModPipeNetworkState existing = manager.get(ModPipeNetworkState.MY_TYPE);

        if (existing == null) {
            existing = new ModPipeNetworkState();
            manager.set(ModPipeNetworkState.MY_TYPE, existing);
        }

        return existing;
    }

    public static void markDirty(ServerWorld world) {
        ModPipeNetworkState state = getOrCreate(world);
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
        ModPipeNetworkState state = getOrCreate(world);

        for (PipeNetwork net : state.getAllNetworks().values()) {
            ModPipeNetworkManager.addExistingNetwork(world, net);
        }
    }
}