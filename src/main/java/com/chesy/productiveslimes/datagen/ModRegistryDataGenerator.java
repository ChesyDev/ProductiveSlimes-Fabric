package com.chesy.productiveslimes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
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
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.DIMENSION_TYPE));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.DENSITY_FUNCTION_TYPE));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.STRUCTURE));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.STRUCTURE_SET));
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.TEMPLATE_POOL));
    }

    @Override
    public String getName() {
        return "";
    }
}
