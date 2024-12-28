package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.screen.ModMenuTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class GuidebookMenu extends ScreenHandler {

    public GuidebookMenu(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory);
    }

    public GuidebookMenu(int syncId, PlayerInventory playerInventory) {
        super(ModMenuTypes.GUIDEBOOK_MENU_HANDLER, syncId);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
