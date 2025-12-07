package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class BaseSlimeOverlayFeatureRenderer extends FeatureRenderer<SlimeEntityRenderState, BaseSlimeModel> {
    private final BaseSlimeModel model;

    public BaseSlimeOverlayFeatureRenderer(FeatureRendererContext<SlimeEntityRenderState, BaseSlimeModel> context, LoadedEntityModels loader, int color) {
        super(context);
        this.model = new BaseSlimeModel(loader.getModelPart(EntityModelLayers.SLIME_OUTER), color);
    }

    public void render(MatrixStack poseStack, OrderedRenderCommandQueue nodeCollector, int light, SlimeEntityRenderState slimeRenderState, float limbAngle, float limbDistance) {
        boolean bl = slimeRenderState.hasOutline() && slimeRenderState.invisible;
        if (!slimeRenderState.invisible || bl) {
            int i = LivingEntityRenderer.getOverlay(slimeRenderState, 0.0F);

            if (bl) {
                nodeCollector.getBatchingQueue(1).submitModel(this.model, slimeRenderState, poseStack, RenderLayers.outlineNoCull(BaseSlimeRenderer.TEXTURE), light, i, this.model.color, null, slimeRenderState.outlineColor, null);
            } else {
                nodeCollector.getBatchingQueue(1).submitModel(this.model, slimeRenderState, poseStack, RenderLayers.entityTranslucent(BaseSlimeRenderer.TEXTURE), light, i, this.model.color, null, slimeRenderState.outlineColor, null);
            }
            nodeCollector.getBatchingQueue(0).submitModel(this.getContextModel(), slimeRenderState, poseStack, RenderLayers.entityTranslucent(BaseSlimeRenderer.TEXTURE), light, i, this.model.color, null, slimeRenderState.outlineColor, null);
        }
    }
}