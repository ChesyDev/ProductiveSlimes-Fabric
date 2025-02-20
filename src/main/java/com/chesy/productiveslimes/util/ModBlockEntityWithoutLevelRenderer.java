package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.block.ModBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.RotationAxis;

public class ModBlockEntityWithoutLevelRenderer implements BuiltinItemRenderer {
    private boolean isRendering = false;

    @Override
    public void render(ItemStack pStack, MatrixStack pPoseStack, VertexConsumerProvider pBuffer, int pPackedLight, int pPackedOverlay) {
        if (isRendering) {
            return;
        }

        isRendering = true;
        try {
            pPoseStack.push();
            BlockState state = ModBlocks.FLUID_TANK.getDefaultState();
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.pop();

            FluidVariant fluidStack = FluidVariant.blank();
            long amount = 0;

            if (pStack.hasNbt() && pStack.getNbt() != null && pStack.getNbt().contains("fluid")) {
                NbtCompound fluidTag = pStack.getNbt().getCompound("fluid");
                ImmutableFluidVariant immutableFluidVariant = ImmutableFluidVariant.fromNbt(fluidTag);
                fluidStack = FluidVariant.of(immutableFluidVariant.fluid());
                amount = immutableFluidVariant.amount();
            }

            if (!fluidStack.isBlank()) {
                float height = ((float) amount / (FluidConstants.BUCKET * 50)) * 0.95f;

                int color = FluidVariantRendering.getColor(fluidStack);
                Sprite sprite = FluidVariantRendering.getSprites(fluidStack)[0];

                VertexConsumer builder = pBuffer.getBuffer(RenderLayers.getFluidLayer(fluidStack.getFluid().getDefaultState()));

                drawQuad(builder, pPoseStack, 0.15f, height, 0.15f, 0.85f, height, 0.85f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color);

                drawQuad(builder, pPoseStack, 0.15f, 0, 0.15f, 0.85f, height, 0.15f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color);
                pPoseStack.push();
                pPoseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                pPoseStack.translate(-1f, 0, -1.6f);
                drawQuad(builder, pPoseStack, 0.15f, 0, 0.75f, 0.85f, height, 0.75f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color);
                pPoseStack.pop();
                pPoseStack.push();
                pPoseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                pPoseStack.translate(-1f, 0, 0);
                drawQuad(builder, pPoseStack, 0.15f, 0, 0.15f, 0.85f, height, 0.15f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color);
                pPoseStack.pop();
                pPoseStack.push();
                pPoseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
                pPoseStack.translate(0, 0, -1f);
                drawQuad(builder, pPoseStack, 0.15f, 0, 0.15f, 0.85f, height, 0.15f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color);
                pPoseStack.pop();
            }
        } finally {
            isRendering = false;
        }
    }

    private static void drawVertex(VertexConsumer builder, MatrixStack poseStack, float x, float y, float z, float u, float v, int packedLight, int color) {
        builder.vertex(poseStack.peek().getPositionMatrix(), x, y, z)
                .color(color)
                .texture(u, v)
                .light(packedLight)
                .normal(1, 0, 0);
    }
    private static void drawQuad(VertexConsumer builder, MatrixStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int packedLight, int color) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, color);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, color);
    }
}
