package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.network.ModNetworkState;
import com.chesy.productiveslimes.network.ModNetworkStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.world.ServerWorld;

public class ModServerLifecycleEvent {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(CustomVariantRegistry::handleDatapack);

        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            ServerWorld overworld = minecraftServer.getOverworld();
            ModNetworkState state = ModNetworkStateManager.getOrCreate(overworld);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            ServerWorld overworld = minecraftServer.getOverworld();
            ModNetworkStateManager.forceSave(overworld);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.getCommandManager().execute(server.getCommandManager().getDispatcher().parse("reload", server.getCommandSource()), "reload");
        });
    }
}
