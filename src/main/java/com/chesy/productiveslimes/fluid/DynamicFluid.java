package com.chesy.productiveslimes.fluid;

import com.chesy.productiveslimes.tier.ModTiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class DynamicFluid extends WaterFluid {
    private final String name;

    public DynamicFluid(String name) {
        this.name = name;
    }

    @Override
    public Fluid getStill() {
        return ModTiers.getSourceByName(name);
    }

    @Override
    protected boolean isInfinite(World world) {
        return false;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public Fluid getFlowing() {
        return ModTiers.getFlowByName(name);
    }

    @Override
    public Item getBucketItem() {
        return ModTiers.getBucketItemByName(name);
    }

    @Override
    public BlockState toBlockState(FluidState state) {
        return ModTiers.getLiquidBlockByName(name).getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    public int getLevel(FluidState state) {
        return 0;
    }

    public static class Flowing extends DynamicFluid {
        public Flowing(String name) {
            super(name);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public int getMaxFlowDistance(WorldView world) {
            return 5;
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends DynamicFluid {
        public Still(String name) {
            super(name);
        }

        @Override
        public int getMaxFlowDistance(WorldView world) {
            return 4;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
