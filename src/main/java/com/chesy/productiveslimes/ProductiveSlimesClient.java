package com.chesy.productiveslimes;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.ModBlockEntities;
import com.chesy.productiveslimes.block.entity.renderer.*;
import com.chesy.productiveslimes.config.CustomVariant;
import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.entity.SlimySpider;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.chesy.productiveslimes.entity.renderer.SlimySkeletonRenderer;
import com.chesy.productiveslimes.entity.renderer.SlimySpiderRenderer;
import com.chesy.productiveslimes.entity.renderer.SlimyZombieRenderer;
import com.chesy.productiveslimes.network.recipe.ClientRecipeManager;
import com.chesy.productiveslimes.network.recipe.RecipeSyncPayload;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.screen.custom.*;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import com.chesy.productiveslimes.datagen.model.special.FluidTankSpecialRenderer;
import com.chesy.productiveslimes.util.IEnergyBlockEntity;
import com.chesy.productiveslimes.util.IFluidBlockEntity;
import com.chesy.productiveslimes.datagen.model.tint.SlimeItemTint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ProductiveSlimesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModMenuTypes.GUIDEBOOK_MENU_HANDLER, GuidebookScreen::new);
        HandledScreens.register(ModMenuTypes.MELTING_STATION_MENU_HANDLER, MeltingStationScreen::new);
        HandledScreens.register(ModMenuTypes.ENERGY_GENERATOR_MENU_HANDLER, EnergyGeneratorScreen::new);
        HandledScreens.register(ModMenuTypes.SOLIDING_STATION_MENU_HANDLER, SolidingStationScreen::new);
        HandledScreens.register(ModMenuTypes.DNA_EXTRACTOR_MENU_HANDLER, DnaExtractorScreen::new);
        HandledScreens.register(ModMenuTypes.DNA_SYNTHESIZER_MENU_HANDLER, DnaSynthesizerScreen::new);
        HandledScreens.register(ModMenuTypes.SLIME_SQUEEZER_MENU_HANDLER, SlimeSqueezerScreen::new);
        HandledScreens.register(ModMenuTypes.SLIMEBALL_COLLECTOR_MENU_HANDLER, SlimeballCollectorScreen::new);
        HandledScreens.register(ModMenuTypes.SLIME_NEST_MENU_HANDLER, SlimeNestScreen::new);

        EntityModelLayerRegistry.registerModelLayer(BaseSlimeModel.SLIME_TEXTURE, BaseSlimeModel::getOuterTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ENERGY_SLIME, ctx -> new BaseSlimeRenderer(ctx, 0xFFffff70));
        entityRender();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DNA_SYNTHESIZER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DNA_EXTRACTOR, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_GENERATOR, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLUID_TANK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOLIDING_STATION, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIMY_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIMY_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIMY_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIMEBALL_COLLECTOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIME_NEST, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_SLIME_BLOCK, RenderLayer.getTranslucent());
        BlockEntityRendererFactories.register(ModBlockEntities.SOLIDING_STATION, SolidingStationBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.FLUID_TANK, FluidTankBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.DNA_EXTRACTOR, DnaExtractorBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.DNA_SYNTHESIZER, DnaSynthesizerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SLIME_SQUEEZER, SlimeSqueezerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SLIMEBALL_COLLECTOR, SlimeballCollectorBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SLIME_NEST, SlimeNestBlockEntityRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xFFffff70, ModBlocks.ENERGY_SLIME_BLOCK);

        // Register the tint source
        TintSourceTypes.ID_MAPPER.put(Identifier.of(ProductiveSlimes.MODID, "slime_item_tint"), SlimeItemTint.MAP_CODEC);

        // Register the special model type
        SpecialModelTypes.ID_MAPPER.put(Identifier.of(ProductiveSlimes.MODID, "fluid_tank"), FluidTankSpecialRenderer.Unbaked.MAP_CODEC);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIMY_PORTAL_FRAME, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIMY_PORTAL, RenderLayer.getTranslucent());

        for (Tier tier : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(tier);
            String name = tiers.name();

            FluidRenderHandlerRegistry.INSTANCE.register(ModTiers.getSourceByName(name), ModTiers.getFlowByName(name), SimpleFluidRenderHandler.coloredWater(tiers.color()));
            BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModTiers.getSourceByName(name), ModTiers.getFlowByName(name));

            BlockRenderLayerMap.INSTANCE.putBlock(ModTiers.getBlockByName(name), RenderLayer.getTranslucent());
            ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> tiers.color(), ModTiers.getBlockByName(name));

            EntityRendererRegistry.register(ModTiers.getEntityByName(name), ctx -> new BaseSlimeRenderer(ctx, tiers.color()));
        }

        for (CustomVariant variant : CustomVariantRegistry.getLoadedTiers()){
            String name = variant.name();

            FluidRenderHandlerRegistry.INSTANCE.register(CustomVariantRegistry.getSourceFluidForVariant(name), CustomVariantRegistry.getFlowingFluidForVariant(name), SimpleFluidRenderHandler.coloredWater(variant.getColor()));
            BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), CustomVariantRegistry.getSourceFluidForVariant(name), CustomVariantRegistry.getFlowingFluidForVariant(name));

            BlockRenderLayerMap.INSTANCE.putBlock(CustomVariantRegistry.getSlimeBlockForVariant(name), RenderLayer.getTranslucent());
            ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> variant.getColor(), CustomVariantRegistry.getSlimeBlockForVariant(name));

            EntityRendererRegistry.register(CustomVariantRegistry.getSlimeForVariant(name), ctx -> new BaseSlimeRenderer(ctx, variant.getColor()));
        }

        ClientPlayNetworking.registerGlobalReceiver(RecipeSyncPayload.TYPE, (recipeSyncPayload, context) -> context.client().execute(() -> ClientRecipeManager.updateRecipes(recipeSyncPayload.recipes())));

        ClientLifecycleEvents.CLIENT_STARTED.register(minecraftClient -> {
            ResourcePackManager dataPackRepository = minecraftClient.getResourcePackManager();
            List<String> selectedIds = new ArrayList<>(dataPackRepository.getEnabledIds());
            String packId = "productiveslimes_resorucepack";
            if (!selectedIds.contains(packId)) {
                selectedIds.add(packId);
                dataPackRepository.setEnabledProfiles(selectedIds);
                minecraftClient.reloadResources();
            }
        });

        // Temporary HUD rendering for snapshot
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client == null || client.world == null || client.player == null) {
                return; // Prevent crashes when world is null
            }

            // Get what the player is looking at
            HitResult hitResult = client.crosshairTarget;

            if (hitResult instanceof BlockHitResult blockHit) {
                World world = client.world;
                BlockEntity blockEntity = world.getBlockEntity(blockHit.getBlockPos());

                if (blockEntity instanceof IEnergyBlockEntity energyStorage) {
                    long energyStored = energyStorage.getEnergyHandler().getAmount();
                    long maxEnergy = energyStorage.getEnergyHandler().getCapacity();

                    // Draw the text on screen
                    int x = 10; // Position on screen (X)
                    int y = 10; // Position on screen (Y)

                    drawContext.drawTextWithShadow(client.textRenderer,
                            Text.of("Energy: " + energyStored + " / " + maxEnergy),
                            x, y, 0xFFFFFF); // White text
                }

                if (blockEntity instanceof IFluidBlockEntity fluidBlockEntity){
                    long fluidAmount = fluidBlockEntity.getFluidHandler().getAmount();
                    long maxFluidAmount = fluidBlockEntity.getFluidHandler().getCapacity();

                    FluidVariant fluidVariant = fluidBlockEntity.getFluidHandler().getResource();
                    String fluidName = Text.translatable(fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey()).getString();

                    // Draw the text on screen
                    int x = 10; // Position on screen (X)
                    int y = 40; // Position on screen (Y)

                    drawContext.drawTextWithShadow(client.textRenderer,
                            Text.translatable("hud.fluid", fluidName, fluidAmount / FluidConstants.BUCKET, maxFluidAmount / FluidConstants.BUCKET),
                            x, y, 0xFFFFFF); // White text
                }
            }
        });
    }

    private void entityRender() {
        EntityRendererRegistry.register(ModEntities.SLIMY_ZOMBIE, ctx -> new SlimyZombieRenderer(ctx));
        EntityRendererRegistry.register(ModEntities.SLIMY_SKELETON, ctx -> new SlimySkeletonRenderer(ctx));
        EntityRendererRegistry.register(ModEntities.SLIMY_SPIDER, ctx -> new SlimySpiderRenderer(ctx));
    }
}
