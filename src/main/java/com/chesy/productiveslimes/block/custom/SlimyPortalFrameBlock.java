package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.util.SlimyPortalShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class SlimyPortalFrameBlock extends Block {
    public SlimyPortalFrameBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!world.isClient) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof SlimyPortalBlock) {
                    ((SlimyPortalBlock) neighborState.getBlock()).removePortal(world, neighborPos);
                }
            }
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && stack.getItem() == Items.SLIME_BALL) {
            Optional<SlimyPortalShape> optional = SlimyPortalShape.findEmptyPortalShape(world, pos.up(), Direction.Axis.X);
            if (optional.isEmpty()) {
                optional = SlimyPortalShape.findEmptyPortalShape(world, pos.up(), Direction.Axis.Z);
            }
            if (optional.isPresent()) {
                SlimyPortalShape portalShape = optional.get();
                portalShape.createPortalBlocks(world);
                world.playSound(
                        null,
                        pos,
                        SoundEvents.ENTITY_SLIME_SQUISH,
                        SoundCategory.BLOCKS,
                        1.0F,
                        world.getRandom().nextFloat() * 0.4F + 0.8F
                );
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return ActionResult.PASS;
    }
}
