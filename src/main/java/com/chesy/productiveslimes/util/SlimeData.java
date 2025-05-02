package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.entity.BaseSlime;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.Objects;

@SuppressWarnings("unchecked")
public record SlimeData(int size, int color, int cooldown, ItemStack dropItem, ItemStack growthItem, EntityType<BaseSlime> slime) {
    public static final Codec<SlimeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("size").forGetter(SlimeData::size),
                    Codec.INT.fieldOf("color").forGetter(SlimeData::color),
                    Codec.INT.fieldOf("cooldown").forGetter(SlimeData::cooldown),
                    ItemStack.CODEC.fieldOf("dropItem").forGetter(SlimeData::dropItem),
                    ItemStack.CODEC.fieldOf("growthItem").forGetter(SlimeData::growthItem),
                    Registries.ENTITY_TYPE.getCodec().fieldOf("slime").forGetter(SlimeData::slime)
            ).apply(instance, (integer, integer2, integer3, itemStack, itemStack2, entityType) -> new SlimeData(integer, integer2, integer3, itemStack, itemStack2, (EntityType<BaseSlime>) entityType))
    );

    public static SlimeData fromSlime(BaseSlime slime) {
        return new SlimeData(
                slime.getSize(),
                slime.getColor(),
                slime.getCooldown(),
                slime.getDropItem(),
                slime.getGrowthItem(),
                slime.getEntityType()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlimeData slimeData = (SlimeData) o;
        return size == slimeData.size && color == slimeData.color && cooldown == slimeData.cooldown && Objects.equals(dropItem, slimeData.dropItem) && Objects.equals(growthItem, slimeData.growthItem) && Objects.equals(slime, slimeData.slime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, color, cooldown, dropItem, growthItem, slime);
    }
}
