package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.entity.BaseSlime;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

@SuppressWarnings("unchecked")
public record SlimeData(int size, int color, int cooldown, ItemStack dropItem, ItemStack growthItem, EntityType<BaseSlime> slime) {
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

    public NbtCompound toTag(NbtCompound tag) {
        tag.putInt("size", size);
        tag.putInt("color", color);
        tag.putInt("cooldown", cooldown);
        tag.put("drop", dropItem.writeNbt(new NbtCompound()));
        tag.put("growth_item", growthItem.writeNbt(new NbtCompound()));
        if (slime != null){
            tag.putString("slime", Registries.ENTITY_TYPE.getId(slime).toString());
        }
        return tag;
    }

    public static SlimeData fromTag(NbtCompound tag) {
        EntityType<BaseSlime> entityType = null;
        if (tag.contains("slime")){
            entityType = (EntityType<BaseSlime>) Registries.ENTITY_TYPE.get(new Identifier(tag.getString("slime")));
        }
        return new SlimeData(
                tag.getInt("size"),
                tag.getInt("color"),
                tag.getInt("cooldown"),
                ItemStack.fromNbt(tag.getCompound("drop")),
                ItemStack.fromNbt(tag.getCompound("growth_item")),
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
