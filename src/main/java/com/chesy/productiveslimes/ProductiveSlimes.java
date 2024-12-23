package com.chesy.productiveslimes;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.ModBlockEntities;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductiveSlimes implements ModInitializer {
	public static final String MOD_ID = "productiveslimes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		System.out.println("ModItems Initialized");
		ModBlocks.initialize();
		System.out.println("ModBlocks Initialized");
		ModBlockEntities.initialize();
		System.out.println("ModBlockEntities Initialized");
		ModMenuTypes.registerScreenHandlers();
		ModEntities.initialize();

		FabricDefaultAttributeRegistry.register(ModEntities.ENERGY_SLIME, BaseSlime.createAttributes());
	}
}