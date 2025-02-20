package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.entity.FluidTankBlockEntity;
import com.chesy.productiveslimes.util.ImmutableFluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidTankBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public FluidTankBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluidTankBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.125D, 0.0D, 0.125D, 0.875D, 1D, .875D);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient){
            bucketUsed(world, pos, player);
        }
        return ActionResult.SUCCESS;
    }

    protected void bucketUsed(World pLevel, BlockPos pPos, PlayerEntity pPlayer){
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity){
            if (pPlayer.getMainHandStack().getItem() instanceof BucketItem bucketItem && bucketItem != Items.BUCKET){
                FluidVariant fluidVariant = fluidTankBlockEntity.getFluidStorage().variant;
                if (!fluidVariant.isBlank()){
                    if (bucketItem.fluid.equals(fluidTankBlockEntity.getFluidStorage().variant.getFluid())){
                        if(fluidTankBlockEntity.getFluidStorage().amount + FluidConstants.BUCKET <= fluidTankBlockEntity.getFluidStorage().getCapacity()){
                            try(Transaction transaction = Transaction.openOuter()){
                                fluidTankBlockEntity.setFluidVariant(fluidVariant);
                                long insertedAmount = fluidTankBlockEntity.getFluidStorage().insert(fluidVariant, FluidConstants.BUCKET, transaction);
                                if (insertedAmount > 0){
                                    transaction.commit();
                                    if (!pPlayer.isCreative()){
                                        pPlayer.getMainHandStack().decrement(1);
                                        pPlayer.giveItemStack(new ItemStack(Items.BUCKET));
                                    }
                                }
                                else {
                                    transaction.abort();
                                }
                            }
                        }
                    }
                }
                else{
                    try(Transaction transaction = Transaction.openOuter()){
                        FluidVariant fluidToInsert = FluidVariant.of(bucketItem.fluid);
                        fluidTankBlockEntity.setFluidVariant(fluidToInsert);
                        long insertedAmount = fluidTankBlockEntity.getFluidStorage().insert(fluidToInsert, FluidConstants.BUCKET, transaction);
                        if (insertedAmount > 0){
                            transaction.commit();
                            if (!pPlayer.isCreative()){
                                pPlayer.getMainHandStack().decrement(1);
                                pPlayer.giveItemStack(new ItemStack(Items.BUCKET));
                            }
                        }
                        else {
                            transaction.abort();
                        }
                    }
                }
            }
            else if (pPlayer.getMainHandStack().getItem() == Items.BUCKET){
                FluidVariant fluidVariant = fluidTankBlockEntity.getFluidStorage().variant;
                if(!fluidVariant.isBlank()){
                    try(Transaction transaction = Transaction.openOuter()){
                        long drainedAmount = fluidTankBlockEntity.getFluidStorage().extract(fluidVariant, FluidConstants.BUCKET, transaction);
                        if (drainedAmount == FluidConstants.BUCKET){
                            transaction.commit();
                            pPlayer.giveItemStack(new ItemStack(fluidVariant.getFluid().getBucketItem()));
                            pPlayer.getMainHandStack().decrement(1);
                            if (fluidTankBlockEntity.getFluidStorage().amount == 0){
                                fluidTankBlockEntity.setFluidVariant(FluidVariant.blank());
                            }
                        }
                        else {
                            transaction.abort();
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);

        if (blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity) {
            ItemStack stack = new ItemStack(this);
            ImmutableFluidVariant immutableFluidStack = new ImmutableFluidVariant(fluidTankBlockEntity.getFluidVariant().getFluid(), fluidTankBlockEntity.getFluidStorage().amount);

            if (immutableFluidStack.fluid() != Fluids.EMPTY) {
                NbtCompound nbt = stack.getOrCreateNbt();

                NbtCompound fluidTag = new NbtCompound();
                immutableFluidStack.toNbt(fluidTag);
                nbt.put("fluid", fluidTag);

                stack.setNbt(nbt);
            }

            drops.clear();
            drops.add(stack);
        }

        return drops;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof FluidTankBlockEntity fluidTankBlockEntity){
            if (itemStack.getNbt() != null && itemStack.getNbt().contains("fluid")){
                NbtCompound nbt = itemStack.getNbt().getCompound("fluid");
                ImmutableFluidVariant immutableFluidVariant = ImmutableFluidVariant.fromNbt(nbt);

                fluidTankBlockEntity.setFluidVariant(FluidVariant.of(immutableFluidVariant.fluid()));
                fluidTankBlockEntity.getFluidStorage().amount = immutableFluidVariant.amount();
                fluidTankBlockEntity.markDirty();
            }
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        if (stack.getNbt() != null && stack.getNbt().contains("fluid") && ImmutableFluidVariant.fromNbt(stack.getNbt().getCompound("fluid")).fluid() != Fluids.EMPTY){
            ImmutableFluidVariant immutableFluidStack = ImmutableFluidVariant.fromNbt(stack.getNbt().getCompound("fluid"));
            FluidVariant fluidStack = FluidVariant.of(immutableFluidStack.fluid());
            tooltip.add(Text.translatable("tooltip.productiveslimes.fluid_stored").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))).append(Text.translatable(fluidStack.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFF)))));
            tooltip.add(Text.translatable("tooltip.productiveslimes.stored_amount").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))).append(Text.translatable("tooltip.productiveslimes.fluid_amount", immutableFluidStack.amount() / FluidConstants.BUCKET).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFF)))));
        }
    }
}
