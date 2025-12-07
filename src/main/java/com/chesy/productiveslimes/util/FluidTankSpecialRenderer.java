package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.renderer.FluidTankBlockEntityRenderer;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record FluidTankSpecialRenderer() implements SpecialModelRenderer<ImmutableFluidVariant> {
    @Override
    public void render(@Nullable ImmutableFluidVariant data, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        matrices.push();
        BlockState blockState = ModBlocks.FLUID_TANK.getDefaultState();
        queue.submitBlockStateModel(matrices, RenderLayers.cutout(), MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState), -1, -1, -1, light, overlay, 0);
        matrices.pop();

        if (data instanceof ImmutableFluidVariant immutableFluidVariant){
            FluidVariant fluidVariant = FluidVariant.of(immutableFluidVariant.fluid());
            FluidTankBlockEntityRenderer.renderFluid(matrices, queue, light, overlay, fluidVariant, immutableFluidVariant.amount());
        }
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(0.0f, 0.0f, 0.0f));
        consumer.accept(new Vector3f(1.0f, 1.0f, 1.0f));
    }

    @Nullable
    @Override
    public ImmutableFluidVariant getData(ItemStack stack) {
        return stack.get(ModDataComponents.FLUID_VARIANT);
    }

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked{
        public static final MapCodec<Unbaked> MAP_CODEC = Identifier.CODEC.fieldOf("texture").xmap(FluidTankSpecialRenderer.Unbaked::new, FluidTankSpecialRenderer.Unbaked::texture);

        @Override
        public @Nullable SpecialModelRenderer<?> bake(BakeContext context) {
            return new FluidTankSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> getCodec() {
            return MAP_CODEC;
        }
    }
}