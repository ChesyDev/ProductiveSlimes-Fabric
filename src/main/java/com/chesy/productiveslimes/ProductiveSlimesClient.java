package com.chesy.productiveslimes;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.ModBlockEntities;
import com.chesy.productiveslimes.block.entity.renderer.DnaExtractorBlockEntityRenderer;
import com.chesy.productiveslimes.block.entity.renderer.DnaSynthesizerBlockEntityRenderer;
import com.chesy.productiveslimes.block.entity.renderer.FluidTankBlockEntityRenderer;
import com.chesy.productiveslimes.block.entity.renderer.SolidingStationBlockEntityRenderer;
import com.chesy.productiveslimes.config.CustomVariant;
import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.screen.custom.*;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ProductiveSlimesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModMenuTypes.GUIDEBOOK_MENU_HANDLER, GuidebookScreen::new);
        HandledScreens.register(ModMenuTypes.MELTING_STATION_MENU_HANDLER, MeltingStationScreen::new);
        HandledScreens.register(ModMenuTypes.ENERGY_GENERATOR_MENU_HANDLER, EnergyGeneratorScreen::new);
        HandledScreens.register(ModMenuTypes.SOLIDING_STATION_MENU_HANDLER, SolidingStationScreen::new);
        HandledScreens.register(ModMenuTypes.DNA_EXTRACTOR_MENU_HANDLER, DnaExtractorScreen::new);
        HandledScreens.register(ModMenuTypes.DNA_SYNTHESIZER_MENU_HANDLER, DnaSynthesizerScreen::new);

        EntityModelLayerRegistry.registerModelLayer(BaseSlimeModel.SLIME_TEXTURE, BaseSlimeModel::getOuterTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ENERGY_SLIME, ctx -> new BaseSlimeRenderer(ctx, 0xFFffff70));

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DNA_SYNTHESIZER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DNA_EXTRACTOR, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_GENERATOR, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLUID_TANK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOLIDING_STATION, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_SLIME_BLOCK, RenderLayer.getTranslucent());
        BlockEntityRendererFactories.register(ModBlockEntities.SOLIDING_STATION, SolidingStationBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.FLUID_TANK, FluidTankBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.DNA_EXTRACTOR, DnaExtractorBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.DNA_SYNTHESIZER, DnaSynthesizerBlockEntityRenderer::new);

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

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            CustomVariantRegistry.handleResourcePack();
        });
    }
}
