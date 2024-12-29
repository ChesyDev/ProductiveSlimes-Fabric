package com.chesy.productiveslimes;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.screen.custom.EnergyGeneratorScreen;
import com.chesy.productiveslimes.screen.custom.GuidebookScreen;
import com.chesy.productiveslimes.tier.ModTierLists;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class ProductiveSlimesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModMenuTypes.GUIDEBOOK_MENU_HANDLER, GuidebookScreen::new);
        HandledScreens.register(ModMenuTypes.ENERGY_GENERATOR_MENU_HANDLER, EnergyGeneratorScreen::new);

        EntityModelLayerRegistry.registerModelLayer(BaseSlimeModel.SLIME_TEXTURE, BaseSlimeModel::getOuterTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ENERGY_SLIME, ctx -> new BaseSlimeRenderer(ctx, 0xFFffff70));

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DNA_SYNTHESIZER, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DNA_EXTRACTOR, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_GENERATOR, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLUID_TANK, RenderLayer.getTranslucent());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_SLIME_BLOCK, RenderLayer.getTranslucent());

        for (Tier tier : Tier.values()){
            ModTiers tiers = ModTierLists.getTierByName(tier);
            String name = tiers.name();

            FluidRenderHandlerRegistry.INSTANCE.register(ModTierLists.getSourceByName(name), ModTierLists.getFlowByName(name), SimpleFluidRenderHandler.coloredWater(tiers.color()));
            BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModTierLists.getSourceByName(name), ModTierLists.getFlowByName(name));

            BlockRenderLayerMap.INSTANCE.putBlock(ModTierLists.getBlockByName(name), RenderLayer.getTranslucent());
            ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> tiers.color(), ModTierLists.getBlockByName(name));

            EntityRendererRegistry.register(ModTierLists.getEntityByName(name), ctx -> new BaseSlimeRenderer(ctx, tiers.color()));
        }
    }
}
