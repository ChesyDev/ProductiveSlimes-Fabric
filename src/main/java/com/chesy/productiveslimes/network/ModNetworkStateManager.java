package com.chesy.productiveslimes.network;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;

public class ModNetworkStateManager {
    private static final String KEY = "my_network_state";

    public static ModNetworkState getOrCreate(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();

        ModNetworkState existing = manager.get(
                ModNetworkState.MY_TYPE,
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
        PersistentStateManager manager = world.getPersistentStateManager();
        manager.save();
    }
}