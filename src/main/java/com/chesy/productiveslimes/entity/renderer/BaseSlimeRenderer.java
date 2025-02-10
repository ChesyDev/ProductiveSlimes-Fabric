package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BaseSlimeRenderer extends MobEntityRenderer<BaseSlime, BaseSlimeModel<BaseSlime>> {
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/entity/template_slime_entity.png");

    public BaseSlimeRenderer(EntityRendererFactory.Context context, int color) {
        super(context, new BaseSlimeModel(context.getPart(EntityModelLayers.SLIME), color), 0.25F);
        this.addFeature(new BaseSlimeOverlayFeatureRenderer(this, context.getModelLoader(), color));
    }

    @Override
    public Identifier getTexture(BaseSlime state) {
        return TEXTURE;
    }

    @Override
    protected void scale(BaseSlime slimeEntity, MatrixStack matrixStack, float f) {
        float g = 0.999F;
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float h = (float)slimeEntity.getSize();
        float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        matrixStack.scale(j * h, 1.0F / j * h, j * h);
    }

    @Override
    public void render(BaseSlime slimeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = 0.25F * (float)slimeEntity.getSize();
        super.render(slimeEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}