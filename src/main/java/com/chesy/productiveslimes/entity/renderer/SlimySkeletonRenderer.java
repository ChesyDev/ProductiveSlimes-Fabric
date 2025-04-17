package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.util.Identifier;

public class SlimySkeletonRenderer extends SkeletonEntityRenderer {
    public SlimySkeletonRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
        return Identifier.of(ProductiveSlimes.MODID, "textures/entity/slimy_skeleton.png");
    }
}
