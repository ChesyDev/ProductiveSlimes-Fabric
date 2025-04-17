package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.util.Identifier;

public class SlimySpiderRenderer extends SpiderEntityRenderer {
    public SlimySpiderRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return Identifier.of(ProductiveSlimes.MODID, "textures/entity/slimy_spider.png");
    }
}
