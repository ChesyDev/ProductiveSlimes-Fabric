package com.chesy.productiveslimes.item;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.entity.ModEntities;
import com.chesy.productiveslimes.item.custom.*;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item GUIDEBOOK = register("guidebook", new GuidebookItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "guidebook")))));

    public static final Item ENERGY_MULTIPLIER_UPGRADE = register("energy_multiplier_upgrade", new EnergyMultiplierUpgrade(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "energy_multiplier_upgrade")))));
    public static final Item SLIME_NEST_SPEED_UPGRADE_1 = register("slime_nest_speed_upgrade_1", new NestUpgradeItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "slime_nest_speed_upgrade_1"))), 1.5f));
    public static final Item SLIME_NEST_SPEED_UPGRADE_2 = register("slime_nest_speed_upgrade_2", new NestUpgradeItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "slime_nest_speed_upgrade_2"))), 2f));
    public static final Item SLIMEBALL_FRAGMENT = register("slimeball_fragment", new Item(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "slimeball_fragment")))));

    public static final Item SLIME_ITEM = register("slime_item", new SlimeItem(new Item.Settings().maxCount(1)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "slime_item")))));

    public static final SpawnEggItem ENERGY_SLIME_SPAWN_EGG = register("energy_slime_spawn_egg", new SpawnEggItem(ModEntities.ENERGY_SLIME, 0xffff70, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "energy_slime_spawn_egg")))));

    public static final DnaItem SLIME_DNA = register("slime_dna", new DnaItem(0xFF7BC35C, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, "slime_dna")))));

    public static void registerTierItems() {
        for (Tier name : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(name);
            String dnaName = tiers.name() + "_slime_dna";
            String spawnEggName = tiers.name() + "_slime_spawn_egg";

            int color = tiers.color();

            DnaItem dna = register(dnaName, new DnaItem(color, new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, dnaName)))));

            SpawnEggItem spawnEgg = register(spawnEggName, new SpawnEggItem(ModTiers.getEntityByName(tiers.name()), color, new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, spawnEggName)))));

            ModTiers.addRegisteredDnaItem(tiers.name(), dna);
            ModTiers.addRegisteredSpawnEggItem(tiers.name(), spawnEgg);
        }
    }

    public static <T extends Item> T register(String id, T item){
        Identifier itemID = Identifier.of(ProductiveSlimes.MODID, id);
        return Registry.register(Registries.ITEM, itemID, item); //returns Registered Item
    }

    public static void initialize() {
        registerTierItems();
    }

    public static void registerSlimeball(){
        for (Tier name : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(name);
            String slimeballName = tiers.name() + "_slimeball";

            int color = tiers.color();

            SlimeballItem slimeball = ModItems.register(slimeballName, new SlimeballItem(color, new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MODID, slimeballName)))));

            ModTiers.addRegisteredSlimeballItem(tiers.name(), slimeball);
        }
    }
}
