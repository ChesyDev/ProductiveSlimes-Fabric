package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.enchantment.ModEnchantments;
import com.chesy.productiveslimes.entity.SlimySkeleton;
import com.chesy.productiveslimes.entity.SlimySpider;
import com.chesy.productiveslimes.entity.SlimyZombie;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimyBlock extends Block {
    public SlimyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if (!entity.isOnGround() || entity.isSpectator() || entity.hasPassengers()) {
            return;
        }

        if (entity instanceof SlimyZombie || entity instanceof SlimySkeleton || entity instanceof SlimySpider) {
            return;
        }

        if (entity instanceof PlayerEntity player) {
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

            RegistryWrapper<Enchantment> enchantRegistry =
                    world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

            RegistryEntry<Enchantment> glidingEntry =
                    enchantRegistry.getOrThrow(ModEnchantments.GLIDING);

            int glideLevel = EnchantmentHelper.getLevel(glidingEntry, boots);

            if (glideLevel > 0) {
                return;
            }
        }

        double slowFactor = 0.05;
        entity.setVelocity(
                entity.getVelocity().multiply(slowFactor, 1.0, slowFactor)
        );
    }
}
