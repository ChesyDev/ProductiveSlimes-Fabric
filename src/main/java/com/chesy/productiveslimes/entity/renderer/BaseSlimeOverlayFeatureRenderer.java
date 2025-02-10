package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;

public class BaseSlimeOverlayFeatureRenderer<T extends BaseSlime> extends FeatureRenderer<T, BaseSlimeModel<T>> {
    private final BaseSlimeModel model;

    public BaseSlimeOverlayFeatureRenderer(FeatureRendererContext<T, BaseSlimeModel<T>> context, EntityModelLoader loader, int color) {
        super(context);
        this.model = new BaseSlimeModel(loader.getModelPart(EntityModelLayers.SLIME_OUTER), color);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = minecraftClient.hasOutline(livingEntity) && livingEntity.isInvisible();
        if (!livingEntity.isInvisible() || bl) {
            VertexConsumer vertexConsumer;
            if (bl) {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(BaseSlimeRenderer.TEXTURE));
            } else {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(BaseSlimeRenderer.TEXTURE));
            }

            this.getContextModel().copyStateTo(this.model);
            this.model.animateModel(livingEntity, f, g, h);
            this.model.setAngles(livingEntity, f, g, j, k, l);
            this.model.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(livingEntity, 0.0F));
        }
    }
}