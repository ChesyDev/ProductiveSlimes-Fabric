package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.worldgen.biome.surface.SurfaceRulesModifier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin {
    @Shadow @Final private GenerationShapeConfig generationShapeConfig;

    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void surfaceRule(CallbackInfoReturnable<MaterialRules.MaterialRule> cir)
    {
        if (this.generationShapeConfig.equals(GenerationShapeConfig.SURFACE) && !FabricLoader.getInstance().isModLoaded("terrablender")){
            cir.setReturnValue(SurfaceRulesModifier.overworld(true, false, true));
        }
    }
}