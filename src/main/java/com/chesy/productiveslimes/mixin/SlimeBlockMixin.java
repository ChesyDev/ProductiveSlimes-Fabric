package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.block.custom.SlimeBlock;
import com.chesy.productiveslimes.entity.SlimyZombie;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeBlock.class)
public class SlimeBlockMixin {
    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (!entity.isOnGround() || entity.isSpectator() || entity.hasPassengers()) {
            return;
        }

        if (entity instanceof SlimyZombie) {
            return;
        }

        double slowFactor = 0.05;
        entity.setVelocity(entity.getVelocity().multiply(slowFactor, 1.0, slowFactor));

        ci.cancel();
    }
}
