package com.chesy.productiveslimes.item;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.item.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item GUIDEBOOK = register("guidebook", new GuidebookItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, "guidebook")))));

    public static final Item ENERGY_MULTIPLIER_UPGRADE = register("energy_multiplier_upgrade", new EnergyMultiplierUpgrade(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, "energy_multiplier_upgrade")))));
    public static final Item SLIMEBALL_FRAGMENT = register("slimeball_fragment", new Item(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, "slimeball_fragment")))));

    public static final Item ENERGY_SLIME_BALL = register("energy_slimeball", new SlimeballItem(0xFFFFFF70, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, "energy_slimeball")))));
    //public static final Item ENERGY_SLIME_SPAWN_EGG = register("energy_slime_spawn_egg", new SpawnEggItem(ModEntities.ENERGY_SLIME.get(), 0xffff70, 0xFFFF00, new Item.Settings()
    //.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, "energy_slime_spawn_egg")))));

    public static final Item SLIME_DNA = register("slime_dna", new DnaItem(0xFF7BC35C, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProductiveSlimes.MOD_ID, "slime_dna")))));

    private static Item register(String id, Item item){
        Identifier itemID = Identifier.of(ProductiveSlimes.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemID, item); //returns Registered Item
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(GUIDEBOOK);
            entries.add(ENERGY_MULTIPLIER_UPGRADE);
            entries.add(SLIMEBALL_FRAGMENT);
            entries.add(ENERGY_SLIME_BALL);
            entries.add(SLIME_DNA);
        });
    }
}
