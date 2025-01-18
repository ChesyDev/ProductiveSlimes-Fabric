package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.network.ModNetworkState;
import com.chesy.productiveslimes.network.ModNetworkStateManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;

public class ModServerLifecycleEvent {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            ServerWorld overworld = minecraftServer.getOverworld();
            ModNetworkState state = ModNetworkStateManager.getOrCreate(overworld);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            ServerWorld overworld = minecraftServer.getOverworld();
            ModNetworkStateManager.forceSave(overworld);
        });
    }
}
