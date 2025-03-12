package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.worldgen.dimension.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SlimyPortalBlock extends Block implements Portal {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    public static final int TELEPORT_DELAY = 80;

    public SlimyPortalBlock(Settings properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.X));
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if (entity.canUsePortals(false)){
            entity.tryUsePortal(this, pos);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public int getPortalDelay(ServerWorld serverLevel, Entity entity) {
        return entity instanceof PlayerEntity player
                ? Math.max(
                0,
                serverLevel.getGameRules()
                        .getInt(
                                player.getAbilities().invulnerable
                                        ? GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY
                                        : GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY
                        )
        )
                : 0;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.get(AXIS)) {
                    case Z:
                        return state.with(AXIS, Direction.Axis.X);
                    case X:
                        return state.with(AXIS, Direction.Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Nullable
    public TeleportTarget createTeleportTarget(ServerWorld serverLevel, Entity entity, BlockPos blockPos) {
        RegistryKey<World> resourcekey = serverLevel.getRegistryKey() == ModDimensions.SLIMY_WORLD ? World.OVERWORLD : ModDimensions.SLIMY_WORLD;
        ServerWorld serverlevel = serverLevel.getServer().getWorld(resourcekey);
        if (serverlevel == null) {
            return null;
        } else {
            boolean flag = serverlevel.getRegistryKey() == ModDimensions.SLIMY_WORLD;
            WorldBorder worldborder = serverlevel.getWorldBorder();
            double d0 = DimensionType.getCoordinateScaleFactor(serverLevel.getDimension(), serverlevel.getDimension());
            BlockPos blockpos = worldborder.clampFloored(entity.getX() * d0, entity.getY(), entity.getZ() * d0);
            return this.getExitPortal(serverlevel, entity, blockPos, blockpos, flag, worldborder);
        }
    }

    private TeleportTarget getExitPortal(ServerWorld level, Entity entity, BlockPos pos, BlockPos exitPos, boolean isSlimyWorld, WorldBorder worldBorder) {
        Optional<BlockPos> optional = findClosestSlimyPortal(level, exitPos, 16);
        BlockLocating.Rectangle blockutil$foundrectangle;
        TeleportTarget.PostDimensionTransition teleporttransition$postteleporttransition;

        if (optional.isPresent()) {
            BlockPos blockpos = optional.get();
            BlockState blockstate = level.getBlockState(blockpos);
            blockutil$foundrectangle = BlockLocating.getLargestRectangle(
                    blockpos,
                    blockstate.get(Properties.HORIZONTAL_AXIS),
                    21,
                    Direction.Axis.Y,
                    21,
                    checkPos -> level.getBlockState(checkPos) == blockstate
            );
            teleporttransition$postteleporttransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(entity1 -> entity1.addPortalChunkTicketAt(blockpos));
        } else {
            Direction.Axis direction$axis = entity.getWorld().getBlockState(pos).getOrEmpty(AXIS).orElse(Direction.Axis.X);
            Optional<BlockLocating.Rectangle> optional1 = buildCustomPortal(level, exitPos, direction$axis);
            if (optional1.isEmpty()) {
                return null;
            }
            blockutil$foundrectangle = optional1.get();
            teleporttransition$postteleporttransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
        }

        return getDimensionTransitionFromExit(entity, pos, blockutil$foundrectangle, level, teleporttransition$postteleporttransition);
    }

    private Optional<BlockPos> findClosestSlimyPortal(ServerWorld level, BlockPos center, int horizontalRadius) {
        BlockPos closest = null;
        double minDistance = Double.MAX_VALUE;
        int cx = center.getX();
        int cz = center.getZ();
        for (int x = cx - horizontalRadius; x <= cx + horizontalRadius; x++) {
            for (int z = cz - horizontalRadius; z <= cz + horizontalRadius; z++) {
                for (int y = 63; y < level.getTopYInclusive(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.getBlockState(pos).getBlock() == ModBlocks.SLIMY_PORTAL) {
                        double distance = pos.getSquaredDistance(center);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closest = pos;
                        }
                    }
                }
            }
        }
        return Optional.ofNullable(closest);
    }

    private Optional<BlockLocating.Rectangle> buildCustomPortal(ServerWorld level, BlockPos pos, Direction.Axis axis) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        double d0 = -1.0;
        BlockPos blockpos = null;
        double d1 = -1.0;
        BlockPos blockpos1 = null;
        WorldBorder worldborder = level.getWorldBorder();
        int i = Math.min(level.getTopYInclusive(), 63 + level.getLogicalHeight() - 1);
        BlockPos.Mutable blockpos$mutableblockpos = pos.mutableCopy();

        int exitChunkX = pos.getX() >> 4;
        int exitChunkZ = pos.getZ() >> 4;

        for (BlockPos.Mutable blockpos$mutableblockpos1 : BlockPos.iterateInSquare(pos, 16, Direction.EAST, Direction.SOUTH)) {
            int currentChunkX = blockpos$mutableblockpos1.getX() >> 4;
            int currentChunkZ = blockpos$mutableblockpos1.getZ() >> 4;
            if (currentChunkX != exitChunkX || currentChunkZ != exitChunkZ) {
                continue;
            }

            int k = Math.min(i, level.getTopY(Heightmap.Type.MOTION_BLOCKING, blockpos$mutableblockpos1.getX(), blockpos$mutableblockpos1.getZ()));
            if (worldborder.contains(blockpos$mutableblockpos1) && worldborder.contains(blockpos$mutableblockpos1.move(direction, 1))) {
                blockpos$mutableblockpos1.move(direction.getOpposite(), 1);

                for (int l = k; l >= 63; l--) {
                    blockpos$mutableblockpos1.setY(l);
                    if (this.canPortalReplaceBlock(level, blockpos$mutableblockpos1)) {
                        int i1 = l;

                        while (l > 63 && this.canPortalReplaceBlock(level, blockpos$mutableblockpos1.move(Direction.DOWN))) {
                            l--;
                        }

                        if (l + 4 <= i) {
                            int j1 = i1 - l;
                            if (j1 <= 0 || j1 >= 3) {
                                blockpos$mutableblockpos1.setY(l);
                                if (this.canHostFrame(level, blockpos$mutableblockpos1, blockpos$mutableblockpos, direction, 0)) {
                                    double d2 = pos.getSquaredDistance(blockpos$mutableblockpos1);
                                    if (this.canHostFrame(level, blockpos$mutableblockpos1, blockpos$mutableblockpos, direction, -1)
                                            && this.canHostFrame(level, blockpos$mutableblockpos1, blockpos$mutableblockpos, direction, 1)
                                            && (d0 == -1.0 || d0 > d2)) {
                                        d0 = d2;
                                        blockpos = blockpos$mutableblockpos1.toImmutable();
                                    }

                                    if (d0 == -1.0 && (d1 == -1.0 || d1 > d2)) {
                                        d1 = d2;
                                        blockpos1 = blockpos$mutableblockpos1.toImmutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (d0 == -1.0 && d1 != -1.0) {
            blockpos = blockpos1;
            d0 = d1;
        }

        if (d0 == -1.0) {
            int k1 = Math.max(63 - -1, 70);
            int i2 = i - 9;
            if (i2 < k1) {
                return Optional.empty();
            }

            blockpos = new BlockPos(pos.getX() - direction.getOffsetX() * 1, MathHelper.clamp(pos.getY(), k1, i2), pos.getZ() - direction.getOffsetZ() * 1)
                    .toImmutable();
            blockpos = worldborder.clampFloored(blockpos);
            Direction direction1 = direction.rotateYClockwise();

            for (int i3 = -1; i3 < 2; i3++) {
                for (int j3 = 0; j3 < 2; j3++) {
                    for (int k3 = -1; k3 < 3; k3++) {
                        BlockState blockstate1 = k3 < 0 ? ModBlocks.SLIMY_PORTAL_FRAME.getDefaultState() : Blocks.AIR.getDefaultState();
                        blockpos$mutableblockpos.set(
                                blockpos, j3 * direction.getOffsetX() + i3 * direction1.getOffsetX(), k3, j3 * direction.getOffsetZ() + i3 * direction1.getOffsetZ()
                        );
                        level.setBlockState(blockpos$mutableblockpos, blockstate1);
                    }
                }
            }
        }

        for (int l1 = -1; l1 < 3; l1++) {
            for (int j2 = -1; j2 < 4; j2++) {
                if (l1 == -1 || l1 == 2 || j2 == -1 || j2 == 3) {
                    blockpos$mutableblockpos.set(blockpos, l1 * direction.getOffsetX(), j2, l1 * direction.getOffsetZ());
                    level.setBlockState(blockpos$mutableblockpos, ModBlocks.SLIMY_PORTAL_FRAME.getDefaultState(), 3);
                }
            }
        }

        BlockState blockstate = ModBlocks.SLIMY_PORTAL.getDefaultState().with(AXIS, axis);

        for (int k2 = 0; k2 < 2; k2++) {
            for (int l2 = 0; l2 < 3; l2++) {
                blockpos$mutableblockpos.set(blockpos, k2 * direction.getOffsetX(), l2, k2 * direction.getOffsetZ());
                level.setBlockState(blockpos$mutableblockpos, blockstate, 18);
            }
        }

        return Optional.of(new BlockLocating.Rectangle(blockpos.toImmutable(), 2, 3));
    }

    private boolean canPortalReplaceBlock(ServerWorld level, BlockPos.Mutable pos) {
        BlockState blockstate = level.getBlockState(pos);
        return blockstate.isReplaceable() && blockstate.getFluidState().isEmpty();
    }

    private boolean canHostFrame(ServerWorld level, BlockPos originalPos, BlockPos.Mutable offsetPos, Direction p_direction, int offsetScale) {
        Direction direction = p_direction.rotateYClockwise();

        for (int i = -1; i < 3; i++) {
            for (int j = -1; j < 4; j++) {
                offsetPos.set(
                        originalPos, p_direction.getOffsetX() * i + direction.getOffsetX() * offsetScale, j, p_direction.getOffsetZ() * i + direction.getOffsetZ() * offsetScale
                );
                if (j < 0 && !level.getBlockState(offsetPos).isSolid()) {
                    return false;
                }

                if (j >= 0 && !canPortalReplaceBlock(level, offsetPos)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static TeleportTarget getDimensionTransitionFromExit(Entity entity, BlockPos pos, BlockLocating.Rectangle rectangle, ServerWorld level, TeleportTarget.PostDimensionTransition postTeleportTransition) {
        BlockState blockstate = entity.getWorld().getBlockState(pos);
        Direction.Axis direction$axis;
        Vec3d vec3;
        if (blockstate.contains(Properties.HORIZONTAL_AXIS)) {
            direction$axis = blockstate.get(Properties.HORIZONTAL_AXIS);
            BlockLocating.Rectangle blockutil$foundrectangle = BlockLocating.getLargestRectangle(
                    pos, direction$axis, 21, Direction.Axis.Y, 21, blockPos -> entity.getWorld().getBlockState(blockPos) == blockstate
            );
            vec3 = entity.positionInPortal(direction$axis, blockutil$foundrectangle);
        } else {
            direction$axis = Direction.Axis.X;
            vec3 = new Vec3d(0.5, 0.0, 0.0);
        }

        return createDimensionTransition(level, rectangle, direction$axis, vec3, entity, postTeleportTransition);
    }

    private static TeleportTarget createDimensionTransition(ServerWorld level, BlockLocating.Rectangle rectangle, Direction.Axis axis, Vec3d offset, Entity entity, TeleportTarget.PostDimensionTransition postTeleportTransition) {
        BlockPos blockpos = rectangle.lowerLeft;
        BlockState blockstate = level.getBlockState(blockpos);
        Direction.Axis direction$axis = blockstate.getOrEmpty(Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
        double d0 = rectangle.width;
        double d1 = rectangle.height;
        EntityDimensions entitydimensions = entity.getDimensions(entity.getPose());
        int i = axis == direction$axis ? 0 : 90;
        double d2 = (double)entitydimensions.width() / 2.0 + (d0 - (double)entitydimensions.width()) * offset.getX();
        double d3 = (d1 - (double)entitydimensions.height()) * offset.getY();
        double d4 = 0.5 + offset.getZ();
        boolean flag = direction$axis == Direction.Axis.X;
        Vec3d vec3 = new Vec3d((double)blockpos.getX() + (flag ? d2 : d4), (double)blockpos.getY() + d3, (double)blockpos.getZ() + (flag ? d4 : d2));
        Vec3d vec31 = NetherPortal.findOpenPosition(vec3, level, entity, entitydimensions);
        return new TeleportTarget(level, vec31, Vec3d.ZERO, (float)i, 0.0F, PositionFlag.combine(PositionFlag.DELTA, PositionFlag.ROT), postTeleportTransition);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos blockPos, Random random) {
        if (random.nextInt(100) == 0) {
            world.playSoundClient(
                    (double)blockPos.getX() + 0.5,
                    (double)blockPos.getY() + 0.5,
                    (double)blockPos.getZ() + 0.5,
                    SoundEvents.BLOCK_PORTAL_AMBIENT,
                    SoundCategory.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        for (int i = 0; i < 4; i++) {
            double d0 = (double)blockPos.getX() + random.nextDouble();
            double d1 = (double)blockPos.getY() + random.nextDouble();
            double d2 = (double)blockPos.getZ() + random.nextDouble();
            double d3 = ((double)random.nextFloat() - 0.5) * 0.5;
            double d4 = ((double)random.nextFloat() - 0.5) * 0.5;
            double d5 = ((double)random.nextFloat() - 0.5) * 0.5;
            int j = random.nextInt(2) * 2 - 1;
            if (!world.getBlockState(blockPos.west()).isOf(this) && !world.getBlockState(blockPos.east()).isOf(this)) {
                d0 = (double)blockPos.getX() + 0.5 + 0.25 * (double)j;
                d3 = random.nextFloat() * 2.0F * (float)j;
            } else {
                d2 = (double)blockPos.getZ() + 0.5 + 0.25 * (double)j;
                d5 = random.nextFloat() * 2.0F * (float)j;
            }

            world.addParticleClient(ParticleTypes.ITEM_SLIME, d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public Effect getPortalEffect() {
        return Portal.Effect.CONFUSION;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = state.get(AXIS);
        boolean bl = axis2 != axis && axis.isHorizontal();
        return !bl && !neighborState.isOf(this) && !NetherPortal.getOnAxis(world, pos, axis2).wasAlreadyValid() ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void removePortal(World level, BlockPos pos) {
        if (level.isClient) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.isOf(this)) {
            return;
        }

        Direction.Axis axis = state.get(AXIS);
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(pos.toImmutable());
        visited.add(pos.toImmutable());

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            level.setBlockState(currentPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.offset(direction);
                if (!visited.contains(neighborPos) && level.getBlockState(neighborPos).isOf(this)) {
                    BlockState neighborState = level.getBlockState(neighborPos);
                    if (neighborState.get(AXIS) == axis) {
                        visited.add(neighborPos);
                        queue.add(neighborPos);
                    }
                }
            }
        }
    }
}