package com.chesy.productiveslimes.mixin;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SlimeEntity.class)
public class SlimeMixin {
    /**
     * @author CoolerProMC
     * @reason Slimes should not attack any entity in this mod
     */
    @Overwrite
    public boolean canAttack() {
        return false;
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 1))
    private void redirectSlimeAttackGoal(GoalSelector instance, int priority, Goal goal) {
        // This effectively skips the addition of the SlimeAttackGoal
        // No operation (NOP), we do nothing here to skip adding the goal
    }

}
