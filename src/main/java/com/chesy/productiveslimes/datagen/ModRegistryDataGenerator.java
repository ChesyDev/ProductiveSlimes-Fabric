package com.chesy.productiveslimes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRegistryDataGenerator extends FabricDynamicRegistryProvider {
    public ModRegistryDataGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.CONFIGURED_FEATURE));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.PLACED_FEATURE));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.BIOME));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS));
//        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.DIMENSION));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.DIMENSION_TYPE));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.DENSITY_FUNCTION_TYPE));
    }

    @Override
    public String getName() {
        return "";
    }
}
