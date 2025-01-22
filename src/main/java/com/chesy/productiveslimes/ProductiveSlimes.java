package com.chesy.productiveslimes;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.ModBlockEntities;
import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.event.EntityInteractEvent;
import com.chesy.productiveslimes.event.ModServerLifecycleEvent;
import com.chesy.productiveslimes.fluid.ModFluids;
import com.chesy.productiveslimes.item.ModItemGroups;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.util.FluidTankTint;
import com.chesy.productiveslimes.util.SlimeItemTint;
import com.chesy.productiveslimes.util.property.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductiveSlimes implements ModInitializer {
	public static final String MODID = "productiveslimes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final SlimeballItem ENERGY_SLIME_BALL = Registry.register(Registries.ITEM, Identifier.of(MODID,"energy_slimeball"), new SlimeballItem(0xFFFFFF70, new Item.Settings()
			.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "energy_slimeball")))));

	@Override
	public void onInitialize() {
		ModTiers.init();
		ModFluids.register();
		ModDataComponents.register();
		ModItemGroups.initialize();

		ModItems.registerSlimeball();
		ModEntities.initialize();
		ModItems.initialize();

		ModBlocks.initialize();
		ModBlockEntities.initialize();

		ModMenuTypes.registerScreenHandlers();
		ModRecipes.register();

		CustomVariantRegistry.initialize();

		FabricDefaultAttributeRegistry.register(ModEntities.ENERGY_SLIME, BaseSlime.createAttributes());

		// Register the event
		ModServerLifecycleEvent.init();
		EntityInteractEvent.init();

		// Register the tint source
		TintSourceTypes.ID_MAPPER.put(Identifier.of(MODID, "slime_item_tint"), SlimeItemTint.MAP_CODEC);
		TintSourceTypes.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_tint"), FluidTankTint.MAP_CODEC);

		// Register property
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_empty"), FluidTankProperty.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_3k"), FluidTankProperty3k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_6k"), FluidTankProperty6k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_9k"), FluidTankProperty9k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_12k"), FluidTankProperty12k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_15k"), FluidTankProperty15k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_18k"), FluidTankProperty18k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_21k"), FluidTankProperty21k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_24k"), FluidTankProperty24k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_27k"), FluidTankProperty27k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_30k"), FluidTankProperty30k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_33k"), FluidTankProperty33k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_36k"), FluidTankProperty36k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_40k"), FluidTankProperty40k.MAP_CODEC);
		BooleanProperties.ID_MAPPER.put(Identifier.of(MODID, "fluid_tank_less_than_45k"), FluidTankProperty45k.MAP_CODEC);
	}
}