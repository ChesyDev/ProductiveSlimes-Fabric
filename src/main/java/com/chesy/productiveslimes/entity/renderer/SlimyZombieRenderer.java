package com.chesy.productiveslimes.entity.renderer;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.entity.SlimyZombie;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.SoftOverride;

public class SlimyZombieRenderer extends ZombieEntityRenderer {
    public SlimyZombieRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
        return Identifier.of(ProductiveSlimes.MODID, "textures/entity/slimy_zombie.png");
    }
}
