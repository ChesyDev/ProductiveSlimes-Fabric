package com.chesy.productiveslimes.util;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerUtils {
    public static void dropContents(World world, BlockPos pos, Inventory inventory) {
        if (!world.isClient) {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) {
                    Block.dropStack(world, pos, stack);
                }
            }
        }
    }
}
