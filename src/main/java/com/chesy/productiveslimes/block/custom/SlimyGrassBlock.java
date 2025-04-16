package com.chesy.productiveslimes.block.custom;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.entity.SlimyZombie;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

import java.util.List;
import java.util.Optional;

public class SlimyGrassBlock extends Block implements Fertilizable {
    public SlimyGrassBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return !state.isOpaqueFullCube();
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if (!entity.isOnGround() || entity.isSpectator() || entity.hasPassengers()) {
            return;
        }

        if (entity instanceof SlimyZombie) {
            return;
        }

        double slowFactor = 0.05;
        entity.setVelocity(
                entity.getVelocity().multiply(slowFactor, 1.0, slowFactor)
        );
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.up();
        BlockState blockState = Blocks.SHORT_GRASS.getDefaultState();
        Optional<RegistryEntry.Reference<PlacedFeature>> optional = world.getRegistryManager().getOrThrow(RegistryKeys.PLACED_FEATURE).getOptional(VegetationPlacedFeatures.GRASS_BONEMEAL);

        label51:
        for(int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;

            for(int j = 0; j < i / 16; ++j) {
                blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!world.getBlockState(blockPos2.down()).isOf(this) || world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
                    continue label51;
                }
            }

            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.isOf(blockState.getBlock()) && random.nextInt(10) == 0) {
                Fertilizable fertilizable = (Fertilizable)blockState.getBlock();
                if (fertilizable.isFertilizable(world, blockPos2, blockState2)) {
                    fertilizable.grow(world, random, blockPos2, blockState2);
                }
            }

            if (blockState2.isAir()) {
                RegistryEntry registryEntry;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> list = ((Biome)world.getBiome(blockPos2).value()).getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty()) {
                        continue;
                    }

                    registryEntry = ((RandomPatchFeatureConfig)((ConfiguredFeature)list.get(0)).config()).feature();
                } else {
                    if (!optional.isPresent()) {
                        continue;
                    }

                    registryEntry = (RegistryEntry)optional.get();
                }

                ((PlacedFeature)registryEntry.value()).generateUnregistered(world, world.getChunkManager().getChunkGenerator(), random, blockPos2);
            }
        }
    }

    private static boolean canBeGrass(BlockState state, WorldView levelReader, BlockPos pos) {
        BlockPos blockpos = pos.up();
        BlockState blockstate = levelReader.getBlockState(blockpos);
        if (blockstate.isOf(Blocks.SNOW) && blockstate.get(SnowBlock.LAYERS) == 1) {
            return true;
        } else if (!blockstate.getFluidState().isEmpty()) {
            return false;
        } else {
            int i = ChunkLightProvider.getRealisticOpacity(state, blockstate, Direction.UP, blockstate.getOpacity());
            return i < 15;
        }
    }

    private static boolean canPropagate(BlockState state, WorldView level, BlockPos pos) {
        BlockPos blockpos = pos.up();
        return canBeGrass(state, level, pos) && !level.getFluidState(blockpos).isIn(FluidTags.WATER);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!canBeGrass(state, world, pos)) {
            if (!world.isChunkLoaded(pos)) return;
            world.setBlockState(pos, ModBlocks.SLIMY_DIRT.getDefaultState());
        } else {
            if (!world.isChunkLoaded(pos)) return;
            if (world.getLightLevel(pos.up()) >= 9) {
                BlockState blockstate = this.getDefaultState();

                for (int i = 0; i < 4; i++) {
                    BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (world.getBlockState(blockpos).isOf(ModBlocks.SLIMY_DIRT) && canPropagate(blockstate, world, blockpos)) {
                        world.setBlockState(blockpos, ModBlocks.SLIMY_GRASS_BLOCK.getDefaultState());
                    }
                }
            }
        }
    }
}
