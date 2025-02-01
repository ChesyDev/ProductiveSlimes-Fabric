package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public abstract class CustomMeleeAttackGoal {
    @Shadow
    public abstract void stop();
    @Accessor
    public abstract PathAwareEntity getMob();
    @Inject(method = "canAttack", at = @At("HEAD"))
    private void canPerformAttack(LivingEntity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof SlimeEntity && !ProductiveSlimes.ironGolemCanAttackSlime) {
            stop();
        }
    }
    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void start(CallbackInfo ci) {
        if (getMob().getTarget() instanceof SlimeEntity && !ProductiveSlimes.ironGolemCanAttackSlime) {
            ci.cancel();
        }
    }
    @Inject(method = "stop", at = @At("HEAD"))
    private void stop(CallbackInfo ci) {
        if (getMob().getTarget() instanceof SlimeEntity && !ProductiveSlimes.ironGolemCanAttackSlime) {
            getMob().setTarget(null);
        }
    }
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (getMob().getTarget() instanceof SlimeEntity && !ProductiveSlimes.ironGolemCanAttackSlime) {
            getMob().setTarget(null);
            ci.cancel();
        }
    }

}
