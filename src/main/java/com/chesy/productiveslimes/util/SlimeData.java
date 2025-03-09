package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.entity.BaseSlime;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

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

    public NbtCompound toTag(NbtCompound tag, RegistryWrapper.WrapperLookup provider) {
        tag.putInt("size", size);
        tag.putInt("color", color);
        tag.putInt("cooldown", cooldown);
        tag.put("drop", dropItem.toNbt(provider));
        tag.put("growth_item", growthItem.toNbt(provider));
        tag.putString("slime", Objects.requireNonNull(Registries.ENTITY_TYPE.getKey(slime).toString()));
        return tag;
    }

    public static SlimeData fromTag(NbtCompound tag, RegistryWrapper.WrapperLookup provider) {
        String slimeTagValue = tag.getString("slime", "");
        if (slimeTagValue.startsWith("Optional[ResourceKey[")) {
            // Strip out the unwanted parts
            int startIndex = slimeTagValue.indexOf('/') + 1; // After '/'
            int endIndex = slimeTagValue.indexOf(']');
            if (startIndex > 0 && endIndex > startIndex) {
                slimeTagValue = slimeTagValue.substring(startIndex, endIndex);
            }
        }
        boolean slime = Registries.ENTITY_TYPE.getEntry(Identifier.of(slimeTagValue.trim())).isPresent();
        EntityType<BaseSlime> entityType;
        if (!slime){
            entityType = null;
        }
        else{
            entityType = (EntityType<BaseSlime>) Registries.ENTITY_TYPE.getEntry(Identifier.of(slimeTagValue.trim())).get().value();
        }
        return new SlimeData(
                tag.getInt("size", 1),
                tag.getInt("color", -1),
                tag.getInt("cooldown", 0),
                ItemStack.fromNbt(provider, tag.getCompoundOrEmpty("drop")).orElse(ItemStack.EMPTY),
                ItemStack.fromNbt(provider, tag.getCompoundOrEmpty("growth_item")).orElse(ItemStack.EMPTY),
                entityType
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
