package com.chesy.productiveslimes.entity.model;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.entity.BaseSlime;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BaseSlimeModel<T extends BaseSlime> extends SinglePartEntityModel<T> {
    public final int color;
    public static final EntityModelLayer SLIME_TEXTURE = new EntityModelLayer(Identifier.of(ProductiveSlimes.MODID, "textures/entity/template_slime_entity.png"), "main");
    private final ModelPart root;

    public BaseSlimeModel(ModelPart root, int color) {
        this.root = root;
        this.color = color;
    }

    public static TexturedModelData getOuterTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public static TexturedModelData getInnerTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 16).cuboid(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.NONE);
        modelPartData.addChild("right_eye", ModelPartBuilder.create().uv(32, 0).cuboid(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F), ModelTransform.NONE);
        modelPartData.addChild("left_eye", ModelPartBuilder.create().uv(32, 4).cuboid(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F), ModelTransform.NONE);
        modelPartData.addChild("mouth", ModelPartBuilder.create().uv(32, 8).cuboid(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float r, float g, float b, float a) {
        int alpha = (this.color >> 24) & 0xFF;
        int red = (this.color >> 16) & 0xFF;
        int green = (this.color >> 8) & 0xFF;
        int blue = this.color & 0xFF;

        float normalizedAlpha = alpha / 255.0f;
        float normalizedRed = red / 255.0f;
        float normalizedGreen = green / 255.0f;
        float normalizedBlue = blue / 255.0f;

        super.render(matrices, vertices, light, overlay, normalizedRed, normalizedGreen, normalizedBlue, normalizedAlpha);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}