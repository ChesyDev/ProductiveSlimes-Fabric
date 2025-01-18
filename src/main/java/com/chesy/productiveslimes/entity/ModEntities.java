package com.chesy.productiveslimes.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.Tier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BaseSlime> ENERGY_SLIME = registerSlime("energy_slime", 1000, 0xffff70, ProductiveSlimes.ENERGY_SLIME_BALL, Items.SLIME_BALL);

    public static void registerTierEntities(){
        for (Tier name : Tier.values()){
            ModTier tiers = ModTiers.getTierByName(name);
            String slimeName = tiers.name() + "_slime";
            Item dropItem = ModTiers.getSlimeballItemByName(tiers.name());
            Item growthItem = ModTiers.getItemByKey(tiers.growthItemKey());

            EntityType<BaseSlime> slime = registerSlime(slimeName, tiers.cooldown(), tiers.color(), dropItem, growthItem);
            ModTiers.addRegisteredSlime(tiers.name(), slime);
        }
    }

    public static EntityType<BaseSlime> registerSlime(String name, int cooldown, int color, Item dropItem, Item growthItem) {
        return Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(ProductiveSlimes.MODID, name),
                EntityType.Builder.<BaseSlime>create((type, world) -> new BaseSlime(type, world, cooldown, color, dropItem, growthItem), SpawnGroup.CREATURE).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProductiveSlimes.MODID, name))));
    }


    public static void initialize() {
        registerTierEntities();
    }
}
