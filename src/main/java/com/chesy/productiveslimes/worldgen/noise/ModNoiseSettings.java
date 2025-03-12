package com.chesy.productiveslimes.worldgen.noise;


import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.worldgen.biome.surface.ModSurfaceRules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.noise.NoiseRouter;

import java.util.List;

public class ModNoiseSettings {
    public static final RegistryKey<ChunkGeneratorSettings> SLIMY_WORLD = RegistryKey.of(RegistryKeys.CHUNK_GENERATOR_SETTINGS, Identifier.of(ProductiveSlimes.MODID, "slimy_world"));

    public static void boostrap(Registerable<ChunkGeneratorSettings> context) {
        context.register(SLIMY_WORLD, slimyWorld(context));
    }

    public static ChunkGeneratorSettings slimyWorld(Registerable<ChunkGeneratorSettings> context) {
        RegistryEntryLookup<DoublePerlinNoiseSampler.NoiseParameters> noiseParamSettings = context.getRegistryLookup(RegistryKeys.NOISE_PARAMETERS);
        RegistryEntryLookup<DensityFunction> densityFunctionSettings = context.getRegistryLookup(RegistryKeys.DENSITY_FUNCTION);

        DensityFunction shiftX = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.SHIFT_X));
        DensityFunction shiftZ = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.SHIFT_Z));
        DensityFunction continent = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.CONTINENTS_OVERWORLD));
        DensityFunction erosion = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.EROSION_OVERWORLD));
        DensityFunction depth = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.DEPTH_OVERWORLD));
        DensityFunction ridges = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.RIDGES_OVERWORLD));
        DensityFunction factor = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.FACTOR_OVERWORLD));
        DensityFunction slopeCheese = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.SLOPED_CHEESE_OVERWORLD_AMPLIFIED));
        DensityFunction entrances = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.CAVES_ENTRANCES_OVERWORLD));
        DensityFunction spaghetti2d = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.CAVES_SPAGHETTI_2D_OVERWORLD));
        DensityFunction spaghettiRoughness = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD));
        DensityFunction pillars = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.CAVES_PILLARS_OVERWORLD));
        DensityFunction noodle = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.CAVES_NOODLE_OVERWORLD));
        DensityFunction y = new DensityFunctionTypes.RegistryEntryHolder(densityFunctionSettings.getOrThrow(DensityFunctions.Y));

        return new ChunkGeneratorSettings(
                new GenerationShapeConfig(-64, 384, 1, 2),
                ModBlocks.SLIMY_STONE.getDefaultState(),
                Blocks.WATER.getDefaultState(),
                new NoiseRouter(
                        DensityFunctionTypes.noise(noiseParamSettings.getOrThrow(NoiseParametersKeys.AQUIFER_BARRIER), 1.0, 0.5),
                        DensityFunctionTypes.noise(noiseParamSettings.getOrThrow(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 1.0, 0.67),
                        DensityFunctionTypes.noise(noiseParamSettings.getOrThrow(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD), 1.0, 0.7142857142857143),
                        DensityFunctionTypes.noise(noiseParamSettings.getOrThrow(NoiseParametersKeys.AQUIFER_LAVA), 1.0, 1.0),
                        DensityFunctionTypes.shiftedNoise(shiftX, shiftZ, 0.25, noiseParamSettings.getOrThrow(NoiseParametersKeys.TEMPERATURE)),
                        DensityFunctionTypes.shiftedNoise(shiftX, shiftZ, 0.25, noiseParamSettings.getOrThrow(NoiseParametersKeys.VEGETATION)),
                        continent,
                        erosion,
                        depth,
                        ridges,
                        DensityFunctionTypes.add(
                                DensityFunctionTypes.constant(0.1171875),
                                DensityFunctionTypes.mul(
                                        DensityFunctionTypes.yClampedGradient(-64, -40, 0.0, 1.0),
                                        DensityFunctionTypes.add(
                                                DensityFunctionTypes.constant(-0.1171875),
                                                DensityFunctionTypes.add(
                                                        DensityFunctionTypes.constant(-0.078125),
                                                        DensityFunctionTypes.mul(
                                                                DensityFunctionTypes.yClampedGradient(240, 256, 1.0, 0.0),
                                                                DensityFunctionTypes.add(
                                                                        DensityFunctionTypes.constant(0.078125),
                                                                        new Clamp(
                                                                                DensityFunctionTypes.add(
                                                                                        DensityFunctionTypes.constant(-0.703125),
                                                                                        DensityFunctionTypes.mul(
                                                                                                DensityFunctionTypes.constant(4.0),
                                                                                                DensityFunctionTypes.mul(
                                                                                                        depth,
                                                                                                        DensityFunctionTypes.cache2d(factor)
                                                                                                ).quarterNegative()
                                                                                        )
                                                                                ),
                                                                                -64.0,
                                                                                64
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ),
                        DensityFunctionTypes.min(
                                DensityFunctionTypes.mul(
                                        DensityFunctionTypes.constant(0.64),
                                        DensityFunctionTypes.interpolated(
                                                DensityFunctionTypes.blendDensity(
                                                        DensityFunctionTypes.add(
                                                                DensityFunctionTypes.constant(0.1171875),
                                                                DensityFunctionTypes.mul(
                                                                        DensityFunctionTypes.yClampedGradient(-64, -40, 0.0, 1.0),
                                                                        DensityFunctionTypes.add(
                                                                                DensityFunctionTypes.constant(-0.1171875),
                                                                                DensityFunctionTypes.add(
                                                                                        DensityFunctionTypes.constant(-0.078125),
                                                                                        DensityFunctionTypes.mul(
                                                                                                DensityFunctionTypes.yClampedGradient(240, 256, 1.0, 0.0),
                                                                                                DensityFunctionTypes.add(
                                                                                                        DensityFunctionTypes.constant(0.078125),
                                                                                                        DensityFunctionTypes.rangeChoice(
                                                                                                                slopeCheese,
                                                                                                                -1000000.0,
                                                                                                                1.5625,
                                                                                                                DensityFunctionTypes.min(
                                                                                                                        slopeCheese,
                                                                                                                        DensityFunctionTypes.mul(
                                                                                                                                DensityFunctionTypes.constant(5.0),
                                                                                                                                entrances
                                                                                                                        )
                                                                                                                ),
                                                                                                                DensityFunctionTypes.max(
                                                                                                                        DensityFunctionTypes.min(
                                                                                                                                DensityFunctionTypes.min(
                                                                                                                                        DensityFunctionTypes.add(
                                                                                                                                                DensityFunctionTypes.mul(
                                                                                                                                                        DensityFunctionTypes.constant(4.0),
                                                                                                                                                        DensityFunctionTypes.noise(
                                                                                                                                                                noiseParamSettings.getOrThrow(NoiseParametersKeys.CAVE_LAYER),
                                                                                                                                                                1.0,8.0
                                                                                                                                                        ).square()
                                                                                                                                                ),
                                                                                                                                                DensityFunctionTypes.add(
                                                                                                                                                        DensityFunctionTypes.add(
                                                                                                                                                                DensityFunctionTypes.constant(0.27),
                                                                                                                                                                DensityFunctionTypes.noise(
                                                                                                                                                                        noiseParamSettings.getOrThrow(NoiseParametersKeys.CAVE_CHEESE),
                                                                                                                                                                        1.0,
                                                                                                                                                                        0.6666666666666666
                                                                                                                                                                )
                                                                                                                                                        ).clamp(-1.0,1.0),
                                                                                                                                                        DensityFunctionTypes.add(
                                                                                                                                                                DensityFunctionTypes.constant(1.5),
                                                                                                                                                                DensityFunctionTypes.mul(
                                                                                                                                                                        DensityFunctionTypes.constant(-0.64),
                                                                                                                                                                        slopeCheese
                                                                                                                                                                )
                                                                                                                                                        ).clamp(0.0, 0.5)
                                                                                                                                                )
                                                                                                                                        ),
                                                                                                                                        entrances
                                                                                                                                ),
                                                                                                                                DensityFunctionTypes.add(
                                                                                                                                        spaghetti2d,
                                                                                                                                        spaghettiRoughness
                                                                                                                                )
                                                                                                                        ),
                                                                                                                        DensityFunctionTypes.rangeChoice(
                                                                                                                                pillars,
                                                                                                                                -1000000.0,
                                                                                                                                0.03,
                                                                                                                                DensityFunctionTypes.constant(-1000000.0),
                                                                                                                                pillars
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                ).squeeze(),
                                noodle
                        ),
                        DensityFunctionTypes.interpolated(
                                DensityFunctionTypes.rangeChoice(
                                        y,
                                        -60.0,
                                        51.0,
                                        DensityFunctionTypes.noise(
                                                noiseParamSettings.getOrThrow(NoiseParametersKeys.ORE_VEININESS),
                                                1.5,
                                                1.5
                                        ),
                                        DensityFunctionTypes.constant(0.0)
                                )
                        ),
                        DensityFunctionTypes.add(
                                DensityFunctionTypes.constant(-0.07999999821186066),
                                DensityFunctionTypes.max(
                                        DensityFunctionTypes.interpolated(
                                                DensityFunctionTypes.rangeChoice(
                                                        y,
                                                        -60.0,
                                                        51.0,
                                                        DensityFunctionTypes.noise(
                                                                noiseParamSettings.getOrThrow(NoiseParametersKeys.ORE_VEIN_A),
                                                                4.0,
                                                                4.0
                                                        ),
                                                        DensityFunctionTypes.constant(0.0)
                                                )
                                        ).abs(),
                                        DensityFunctionTypes.interpolated(
                                                DensityFunctionTypes.rangeChoice(
                                                        y,
                                                        -60.0,
                                                        51.0,
                                                        DensityFunctionTypes.noise(
                                                                noiseParamSettings.getOrThrow(NoiseParametersKeys.ORE_VEIN_B),
                                                                4.0,
                                                                4.0
                                                        ),
                                                        DensityFunctionTypes.constant(0.0)
                                                )
                                        ).abs()
                                )
                        ),
                        DensityFunctionTypes.noise(
                                noiseParamSettings.getOrThrow(NoiseParametersKeys.ORE_GAP),
                                1.0,
                                1.0
                        )
                ),
                ModSurfaceRules.makeRules(true, false, true),
                List.of(
                        new MultiNoiseUtil.NoiseHypercube(
                                MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(-0.11f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(0.0f),
                                MultiNoiseUtil.ParameterRange.of(-1.0f, -0.16f),
                                0
                        ),
                        new MultiNoiseUtil.NoiseHypercube(
                                MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(-0.11f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f),
                                MultiNoiseUtil.ParameterRange.of(0.0f),
                                MultiNoiseUtil.ParameterRange.of(0.16f, 1.0f),
                                0
                        )
                ),
                63,
                true,
                true,
                false,
                false
        );
    }

    public record Clamp(DensityFunction input, double minValue, double maxValue) implements DensityFunctionTypes.Unary {
        static final Codec<Double> CONSTANT_RANGE = Codec.doubleRange(-1000000.0, 1000000.0);

        private static final MapCodec<Clamp> CLAMP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(DensityFunction.CODEC.fieldOf("input").forGetter(Clamp::input), CONSTANT_RANGE.fieldOf("min").forGetter(Clamp::minValue), CONSTANT_RANGE.fieldOf("max").forGetter(Clamp::maxValue)).apply(instance, Clamp::new);
        });
        public static final CodecHolder<Clamp> CODEC_HOLDER;

        public double apply(double density) {
            return MathHelper.clamp(density, this.minValue, this.maxValue);
        }

        public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
            return new Clamp(this.input.apply(visitor), this.minValue, this.maxValue);
        }

        public CodecHolder<? extends DensityFunction> getCodecHolder() {
            return CODEC_HOLDER;
        }

        public DensityFunction input() {
            return this.input;
        }

        public double minValue() {
            return this.minValue;
        }

        public double maxValue() {
            return this.maxValue;
        }

        static {
            CODEC_HOLDER = CodecHolder.of(CLAMP_CODEC);
        }
    }
}