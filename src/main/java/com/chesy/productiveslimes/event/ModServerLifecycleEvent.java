package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.config.asset.CustomVariantDataPack;
import com.chesy.productiveslimes.dataattachment.ModDataAttachments;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.network.cable.ModCableNetworkManager;
import com.chesy.productiveslimes.network.cable.ModCableNetworkStateManager;
import com.chesy.productiveslimes.network.pipe.ModPipeNetworkManager;
import com.chesy.productiveslimes.network.pipe.ModPipeNetworkStateManager;
import com.chesy.productiveslimes.network.recipe.RecipeSyncPayload;
import com.chesy.productiveslimes.util.PackRepositoryAccess;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.resource.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModServerLifecycleEvent {
    public static void init() {
        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            ServerWorld overworld = minecraftServer.getOverworld();
            ModCableNetworkStateManager.forceSave(overworld);
            ModPipeNetworkStateManager.forceSave(overworld);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
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

            ResourcePackManager dataPackRepository = minecraftServer.getDataPackManager();
            List<String> selectedIds = new ArrayList<>(dataPackRepository.getEnabledIds());
            String packId = "productiveslimes_datapack";
            if (!selectedIds.contains(packId)) {
                selectedIds.add(packId);
                dataPackRepository.setEnabledProfiles(selectedIds);
                minecraftServer.reloadResources(selectedIds);
            }
        });

        PayloadTypeRegistry.playS2C().register(RecipeSyncPayload.TYPE, RecipeSyncPayload.STREAM_CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerRecipeManager recipeManager = server.getRecipeManager();
            ServerPlayNetworking.send(handler.player, new RecipeSyncPayload(recipeManager.values().stream().toList()));

            PlayerEntity player = handler.player;
            Boolean isFirstTimeLogin = player.getAttachedOrSet(ModDataAttachments.IS_FIRST_TIME_LOGIN, true);

            if (isFirstTimeLogin) {
                player.giveItemStack(new ItemStack(ModItems.GUIDEBOOK));
                player.setAttached(ModDataAttachments.IS_FIRST_TIME_LOGIN, false);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            CustomVariantDataPack dataPack = new CustomVariantDataPack(CustomVariantRegistry.dataPackResources);
            ResourcePackProfile pack = ResourcePackProfile.create(
                    new ResourcePackInfo(
                            "productiveslimes_datapack",
                            Text.literal("In Memory Data Pack"),
                            ResourcePackSource.SERVER,
                            Optional.empty()
                    ),
                    new ResourcePackProfile.PackFactory() {
                        @Override
                        public ResourcePack open(ResourcePackInfo info) {
                            return dataPack;
                        }

                        @Override
                        public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                            return dataPack;
                        }
                    },
                    ResourceType.SERVER_DATA,
                    new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
            );

            ResourcePackManager dataPackRepository = minecraftServer.getDataPackManager();
            ResourcePackProvider customSource = (packSelectionConfig) -> packSelectionConfig.accept(pack);
            ((PackRepositoryAccess) dataPackRepository).addRepositorySource(customSource);
            minecraftServer.reloadResources(dataPackRepository.getEnabledIds());
        });
    }
}
