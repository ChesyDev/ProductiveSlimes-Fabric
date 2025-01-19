package com.chesy.productiveslimes.mixin;

import net.minecraft.entity.ai.goal.WanderNearTargetGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WanderNearTargetGoal.class)
public abstract class CustomMoveTowardsTargetGoalMixin {
    @Shadow
    public abstract void stop();
    @Accessor
    public abstract PathAwareEntity getMob();
    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void start(CallbackInfo ci) {
        if (getMob().getTarget() instanceof SlimeEntity) {
            ci.cancel();
        }
    }
    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    private void canUse(CallbackInfoReturnable<Boolean> cir) {
        if (getMob().getTarget() instanceof SlimeEntity) {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "shouldContinue", at = @At("HEAD"), cancellable = true)
    private void canContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        if (getMob().getTarget() instanceof SlimeEntity) {
            cir.setReturnValue(false);
        }
    }
}
