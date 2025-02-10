package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.network.ModNetworkManager;
import com.chesy.productiveslimes.network.ModNetworkStateManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.world.ServerWorld;

public class ModServerLifecycleEvent {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            CustomVariantRegistry.handleDatapack(minecraftServer);
            minecraftServer.getCommandManager().execute(minecraftServer.getCommandManager().getDispatcher().parse("reload", minecraftServer.getCommandSource()), "reload");
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            ServerWorld overworld = minecraftServer.getOverworld();
            ModNetworkStateManager.forceSave(overworld);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // For each loaded ServerWorld
            for (ServerWorld world : server.getWorlds()) {
                ModNetworkManager.tickAllNetworks(world);
            }
        });

        ServerWorldEvents.LOAD.register((minecraftServer, serverWorld) -> {
            if (!serverWorld.isClient){
                ModNetworkStateManager.loadAllNetworksToManager(serverWorld);
            }
        });
    }
}
