package com.chesy.productiveslimes.handler;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public class MyNetworkStateManager {
    private static final String KEY = "my_network_state";

    /**
     * Tries to load an existing MyNetworkState from the disk.
     * If none found, creates a new one.
     */
    public static MyNetworkState getOrCreate(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();

        // The typical older signature:
        // T get(Function<NbtCompound, T> factory, String key)
        // So we pass a small lambda that calls our fromNbt(...) or readNbt(...) constructor.
        MyNetworkState existing = manager.get(
                MyNetworkState.MY_TYPE,
                KEY
        );

        if (existing == null) {
            // Not found -> create new
            existing = new MyNetworkState();
            manager.set(KEY, existing);
        }

        return existing;
    }

    public static void markDirty(ServerWorld world) {
        MyNetworkState state = getOrCreate(world);
        // Mark it dirty so it will be saved again
        state.setDirty(true);
    }

    public static void forceSave(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        // Some older versions have manager.save() that forcibly writes all dirty states:
        manager.save();
        // If you get a compile error, your version doesn't have this method.
    }
}