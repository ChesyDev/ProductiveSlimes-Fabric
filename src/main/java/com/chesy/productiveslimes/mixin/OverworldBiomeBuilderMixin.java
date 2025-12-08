package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.worldgen.biome.OverworldBiomeInjector;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(VanillaBiomeParameters.class)
public class OverworldBiomeBuilderMixin {
    @Inject(method = "writeOverworldBiomeParameters", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/util/VanillaBiomeParameters;writeOceanBiomes(Ljava/util/function/Consumer;)V"))
    private void injectCustomBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, CallbackInfo ci) {
        OverworldBiomeInjector.injectBiomes(consumer);
    }
}