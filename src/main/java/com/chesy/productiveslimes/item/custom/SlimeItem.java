package com.chesy.productiveslimes.item.custom;

import com.chesy.productiveslimes.entity.BaseSlime;
import com.chesy.productiveslimes.util.SlimeData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.Objects;

public class SlimeItem extends Item {
    public SlimeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld() instanceof ServerWorld serverWorld){
            ItemStack itemStack = context.getStack();

            if (!itemStack.hasNbt() || itemStack.getNbt() == null || !itemStack.getNbt().contains("slime_data")){
                return ActionResult.PASS;
            }

            SlimeData slimeData = SlimeData.fromTag(itemStack.getNbt().getCompound("slime_data"));

            BaseSlime entity = slimeData.slime().create(serverWorld, null, null, context.getBlockPos(), SpawnReason.MOB_SUMMONED, true, false);

            assert entity != null;
            entity.setSize(slimeData.size(), true);
            context.getWorld().spawnEntity(entity);
            Objects.requireNonNull(context.getPlayer()).setStackInHand(context.getHand(), ItemStack.EMPTY);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Text getName(ItemStack stack) {
        if (!stack.hasNbt() || stack.getNbt() == null || !stack.getNbt().contains("slime_data")){
            return Text.translatable("item.productiveslimes.slime_item");
        }

        SlimeData slimeData = SlimeData.fromTag(stack.getNbt().getCompound("slime_data"));
        if (slimeData == null){
            return Text.translatable("item.productiveslimes.slime_item");
        }
        return slimeData.slime().getName();
    }
}
