package com.chesy.productiveslimes;

import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.screen.custom.GuidebookMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ProductiveSlimesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModMenuTypes.GUIDEBOOK_MENU_HANDLER, GuidebookMenu::new);

        EntityModelLayerRegistry.registerModelLayer(BaseSlimeModel.SLIME_TEXTURE, BaseSlimeModel::getOuterTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ENERGY_SLIME, ctx -> new BaseSlimeRenderer(ctx, 0xFFffff70));
    }
}
