package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.network.cable.ModCableNetworkManager;
import com.chesy.productiveslimes.network.cable.ModCableNetworkStateManager;
import com.chesy.productiveslimes.network.pipe.ModPipeNetworkManager;
import com.chesy.productiveslimes.network.pipe.ModPipeNetworkStateManager;
import com.chesy.productiveslimes.network.recipe.RecipeSyncPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.server.world.ServerWorld;

public class ModServerLifecycleEvent {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(CustomVariantRegistry::handleDatapack);

        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            minecraftServer.getCommandManager().execute(minecraftServer.getCommandManager().getDispatcher().parse("reload", minecraftServer.getCommandSource()), "reload");
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            ServerWorld overworld = minecraftServer.getOverworld();
            ModCableNetworkStateManager.forceSave(overworld);
            ModPipeNetworkStateManager.forceSave(overworld);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // For each loaded ServerWorld
            for (ServerWorld world : server.getWorlds()) {
                ModCableNetworkManager.tickAllNetworks(world);
                ModPipeNetworkManager.tickAllNetworks(world);
            }
        });


        ServerWorldEvents.LOAD.register((minecraftServer, serverWorld) -> {
            if (!serverWorld.isClient){
                ModCableNetworkStateManager.loadAllNetworksToManager(serverWorld);
                ModPipeNetworkStateManager.loadAllNetworksToManager(serverWorld);
            }
        });

        PayloadTypeRegistry.playS2C().register(RecipeSyncPayload.TYPE, RecipeSyncPayload.STREAM_CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.getCommandManager().execute(server.getCommandManager().getDispatcher().parse("reload", server.getCommandSource()), "reload");
            ServerRecipeManager recipeManager = server.getRecipeManager();

            ServerPlayNetworking.send(handler.player, new RecipeSyncPayload(recipeManager.values().stream().toList()));
        });
    }
}
