package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BaseSlimeRenderer extends MobEntityRenderer<SlimeEntity, SlimeEntityRenderState, BaseSlimeModel> {
    public static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/entity/template_slime_entity.png");

    public BaseSlimeRenderer(EntityRendererFactory.Context context, int color) {
        super(context, new BaseSlimeModel(context.getPart(EntityModelLayers.SLIME), color), 0.25F);
        this.addFeature(new BaseSlimeOverlayFeatureRenderer(this, context.getEntityModels(), color));
    }

    protected float getShadowRadius(SlimeEntityRenderState slimeEntityRenderState) {
        return (float)slimeEntityRenderState.size * 0.25F;
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState state) {
        return TEXTURE;
    }

    protected void scale(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack) {
        float f = 0.999F;
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float g = (float)slimeEntityRenderState.size;
        float h = slimeEntityRenderState.stretch / (g * 0.5F + 1.0F);
        float i = 1.0F / (h + 1.0F);
        matrixStack.scale(i * g, 1.0F / i * g, i * g);
    }

    public SlimeEntityRenderState createRenderState() {
        return new SlimeEntityRenderState();
    }

    public void updateRenderState(SlimeEntity slimeEntity, SlimeEntityRenderState slimeEntityRenderState, float f) {
        super.updateRenderState(slimeEntity, slimeEntityRenderState, f);
        slimeEntityRenderState.stretch = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch);
        slimeEntityRenderState.size = slimeEntity.getSize();
    }
}