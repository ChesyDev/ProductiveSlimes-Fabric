package com.chesy.productiveslimes;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.ModBlockEntities;
import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.config.ModConfig;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.event.EntityInteractEvent;
import com.chesy.productiveslimes.event.ModServerLifecycleEvent;
import com.chesy.productiveslimes.event.ModVillagerTrade;
import com.chesy.productiveslimes.fluid.ModFluids;
import com.chesy.productiveslimes.item.ModItemGroups;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.villager.ModVillagers;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ProductiveSlimes implements ModInitializer {
	public static final String MODID = "productiveslimes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final Path CONFIG_PATH = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), "productiveslimes.toml");
	public static FileConfig config;
	public static boolean vanillaSlimeCanAttackPlayer;
	public static boolean ironGolemCanAttackSlime;

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

		ModVillagers.init();

		if (!Boolean.getBoolean("fabric.datagen")){
			CustomVariantRegistry.initialize();
			ModConfig.config();
		}

		FabricDefaultAttributeRegistry.register(ModEntities.ENERGY_SLIME, BaseSlime.createAttributes());

//		SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MODID, ModSurfaceRules.makeRules());

		// Register the event
		ModServerLifecycleEvent.init();
		EntityInteractEvent.init();
		ModVillagerTrade.init();

		// Strippable property
		StrippableBlockRegistry.register(ModBlocks.SLIMY_LOG, ModBlocks.STRIPPED_SLIMY_LOG);
		StrippableBlockRegistry.register(ModBlocks.SLIMY_WOOD, ModBlocks.STRIPPED_SLIMY_WOOD);

		// Flammable property
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SLIMY_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SLIMY_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_SLIMY_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SLIMY_PLANKS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SLIMY_LEAVES, 30, 60);
	}
}