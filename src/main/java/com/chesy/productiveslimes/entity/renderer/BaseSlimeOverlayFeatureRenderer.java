package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
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

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SlimeEntityRenderState slimeEntityRenderState, float f, float g) {
        boolean bl = slimeEntityRenderState.hasOutline && slimeEntityRenderState.invisible;
        if (!slimeEntityRenderState.invisible || bl) {
            VertexConsumer vertexConsumer;
            if (bl) {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(BaseSlimeRenderer.TEXTURE));
            } else {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(BaseSlimeRenderer.TEXTURE));
            }

            this.model.setAngles(slimeEntityRenderState);
            this.model.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(slimeEntityRenderState, 0.0F), model.color);
            this.getContextModel().getRoot().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(slimeEntityRenderState, 0.0F), model.color);
        }
    }
}