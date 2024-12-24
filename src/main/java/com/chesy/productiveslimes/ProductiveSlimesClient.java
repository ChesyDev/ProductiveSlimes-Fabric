package com.chesy.productiveslimes;

import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.screen.custom.GuidebookMenu;
import com.chesy.productiveslimes.tier.ModTierLists;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class ProductiveSlimesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModMenuTypes.GUIDEBOOK_MENU_HANDLER, GuidebookMenu::new);

        EntityModelLayerRegistry.registerModelLayer(BaseSlimeModel.SLIME_TEXTURE, BaseSlimeModel::getOuterTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ENERGY_SLIME, ctx -> new BaseSlimeRenderer(ctx, 0xFFffff70));

        for (Tier tier : Tier.values()){
            ModTiers tiers = ModTierLists.getTierByName(tier);
            String name = tiers.name();

            FluidRenderHandlerRegistry.INSTANCE.register(ModTierLists.getSourceByName(name), ModTierLists.getFlowByName(name),
                    SimpleFluidRenderHandler.coloredWater(tiers.color()));
            BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                    ModTierLists.getSourceByName(name), ModTierLists.getFlowByName(name));
        }
    }
}
