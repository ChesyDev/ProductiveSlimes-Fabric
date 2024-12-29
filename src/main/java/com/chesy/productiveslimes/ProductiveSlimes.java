package com.chesy.productiveslimes;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.ModBlockEntities;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.fluid.ModFluids;
import com.chesy.productiveslimes.item.ModItemGroups;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.item.custom.DnaItem;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.item.custom.SpawnEggItem;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.tier.ModTierLists;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductiveSlimes implements ModInitializer {
	public static final String MOD_ID = "productiveslimes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final SlimeballItem ENERGY_SLIME_BALL = Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"energy_slimeball"), new SlimeballItem(0xFFFFFF70, new Item.Settings()
			.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, "energy_slimeball")))));

	@Override
	public void onInitialize() {
		ModTierLists.init();
		ModFluids.register();
		ModDataComponents.register();
		ModItemGroups.initialize();

		ModItems.registerSlimeball();
		ModEntities.initialize();
		ModItems.initialize();

		ModBlocks.initialize();
		ModBlockEntities.initialize();

		ModMenuTypes.registerScreenHandlers();

		FabricDefaultAttributeRegistry.register(ModEntities.ENERGY_SLIME, BaseSlime.createAttributes());
	}
}