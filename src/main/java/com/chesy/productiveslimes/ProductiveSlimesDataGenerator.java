package com.chesy.productiveslimes;

import com.chesy.productiveslimes.datagen.*;
import com.chesy.productiveslimes.worldgen.ModConfiguredFeatures;
import com.chesy.productiveslimes.worldgen.ModPlacedFeatures;
import com.chesy.productiveslimes.worldgen.biome.ModBiomes;
import com.chesy.productiveslimes.worldgen.dimension.ModDimensionTypes;
import com.chesy.productiveslimes.worldgen.dimension.ModDimensions;
import com.chesy.productiveslimes.worldgen.noise.ModNoiseSettings;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class ProductiveSlimesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModFluidTagProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModRegistryDataGenerator::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.BIOME, ModBiomes::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.CHUNK_GENERATOR_SETTINGS, ModNoiseSettings::boostrap);
		registryBuilder.addRegistry(RegistryKeys.DIMENSION_TYPE, ModDimensionTypes::boostrap);
		registryBuilder.addRegistry(RegistryKeys.DIMENSION, ModDimensions::boostrap);
	}
}
