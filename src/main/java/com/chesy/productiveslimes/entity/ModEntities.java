package com.chesy.productiveslimes.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BaseSlime> ENERGY_SLIME = registerSlime("energy_slime", 1000, 0xffff70, Items.SLIME_BALL, Items.SLIME_BALL);

    public static EntityType<BaseSlime> registerSlime(String name, int cooldown, int color, Item dropItem, Item growthItem) {
        return Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(ProductiveSlimes.MOD_ID, name),
                EntityType.Builder.<BaseSlime>create((type, world) -> new BaseSlime(type, world, cooldown, color, dropItem, growthItem), SpawnGroup.CREATURE).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProductiveSlimes.MOD_ID, name))));
    }

    public static void register() {

    }

    public static void initialize() {

    }
}
