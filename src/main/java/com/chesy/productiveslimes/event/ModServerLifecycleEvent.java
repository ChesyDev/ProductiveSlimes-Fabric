package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.network.ModNetworkState;
import com.chesy.productiveslimes.network.ModNetworkStateManager;
import com.chesy.productiveslimes.network.RecipeSyncPayload;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.server.network.ServerPlayerEntity;
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

        PayloadTypeRegistry.playS2C().register(RecipeSyncPayload.TYPE, RecipeSyncPayload.STREAM_CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.getCommandManager().execute(server.getCommandManager().getDispatcher().parse("reload", server.getCommandSource()), "reload");
            ServerRecipeManager recipeManager = server.getRecipeManager();

            ServerPlayNetworking.send(handler.player, new RecipeSyncPayload(recipeManager.values().stream().toList()));
        });
    }
}
