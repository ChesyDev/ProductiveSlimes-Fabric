package com.chesy.productiveslimes;

import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductiveSlimes implements ModInitializer {
	public static final String MOD_ID = "productiveslimes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModMenuTypes.registerScreenHandlers();
	}
}