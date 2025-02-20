package com.chesy.productiveslimes.event;

import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.util.SlimeData;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class EntityInteractEvent {
    public static void init(){
        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            if (!(entity instanceof BaseSlime baseSlime)) return ActionResult.PASS;
            if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
            if (!playerEntity.isSneaking()) return ActionResult.PASS;
            if (playerEntity.getStackInHand(hand).getItem() != Items.AIR) return ActionResult.PASS;

            ItemStack itemStack = new ItemStack(ModItems.SLIME_ITEM);
            NbtCompound tag = new NbtCompound();
            tag.put("slime_data", SlimeData.fromSlime(baseSlime).toTag(new NbtCompound()));
            itemStack.setNbt(tag);

            playerEntity.setStackInHand(hand, itemStack);
            entity.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
            return ActionResult.SUCCESS;
        });
    }
}
