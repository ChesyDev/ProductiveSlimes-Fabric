package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.util.ModTags;
import com.chesy.productiveslimes.worldgen.biome.ModBiomes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlimeEntity.class)
public class SlimeMixin {
    /**
     * @author CoolerProMC
     * @reason Slimes should not attack any entity in this mod
     */
    @Overwrite
    public boolean canAttack() {
        return ProductiveSlimes.vanillaSlimeCanAttackPlayer;
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 1))
    private void redirectSlimeAttackGoal(GoalSelector instance, int priority, Goal goal) {
        if (ProductiveSlimes.vanillaSlimeCanAttackPlayer) {
            instance.add(priority, goal);
        }
    }

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void onCheckSpawnRules(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBiome(pos).matchesKey(ModBiomes.SLIMY_LAND)) {
            cir.setReturnValue(true);
        }
    }
}
