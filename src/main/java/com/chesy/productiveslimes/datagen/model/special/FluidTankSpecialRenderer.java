package com.chesy.productiveslimes.datagen.model.special;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.renderer.FluidTankBlockEntityRenderer;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.datacomponent.custom.ImmutableFluidVariant;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record FluidTankSpecialRenderer() implements SpecialModelRenderer {
    @Override
    public void render(@Nullable Object data, ModelTransformationMode modelTransformationMode, MatrixStack poseStack, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        poseStack.push();
        BlockState blockState = ModBlocks.FLUID_TANK.getDefaultState();
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, poseStack, vertexConsumers, light, overlay);
        poseStack.pop();

        if (data instanceof ImmutableFluidVariant immutableFluidVariant){
            FluidVariant fluidVariant = FluidVariant.of(immutableFluidVariant.fluid());
            FluidTankBlockEntityRenderer.renderFluid(poseStack, vertexConsumers, light, overlay, fluidVariant, immutableFluidVariant.amount());
        }
    }

    @Nullable
    @Override
    public ImmutableFluidVariant getData(ItemStack stack) {
        return stack.get(ModDataComponents.FLUID_VARIANT);
    }

    public record Unbaked(Identifier texture) implements  SpecialModelRenderer.Unbaked{
        public static final MapCodec<Unbaked> MAP_CODEC = Identifier.CODEC.fieldOf("texture").xmap(FluidTankSpecialRenderer.Unbaked::new, FluidTankSpecialRenderer.Unbaked::texture);

        @Nullable
        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new FluidTankSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> getCodec() {
            return MAP_CODEC;
        }
    }
}
