package com.chesy.productiveslimes.entity.model;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

public class BaseSlimeModel extends EntityModel<EntityRenderState> {
    public final int color;
    public static final EntityModelLayer SLIME_TEXTURE = new EntityModelLayer(Identifier.of(ProductiveSlimes.MODID, "textures/entity/template_slime_entity.png"), "main");
    private final ModelPart root;

    public BaseSlimeModel(ModelPart root, int color) {
        super(root);
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

    public ModelPart getRoot() {
        return root;
    }
}