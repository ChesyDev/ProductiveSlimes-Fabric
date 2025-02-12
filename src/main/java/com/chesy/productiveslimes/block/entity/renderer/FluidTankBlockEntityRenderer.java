package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.FluidTankBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class FluidTankBlockEntityRenderer implements BlockEntityRenderer<FluidTankBlockEntity> {
    private final BlockEntityRendererFactory.Context context;

    public FluidTankBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(FluidTankBlockEntity pBlockEntity, float pPartialTick, MatrixStack pPoseStack, VertexConsumerProvider pBufferSource, int pPackedLight, int pPackedOverlay, Vec3d vec3d) {
        FluidVariant fluidStack = pBlockEntity.getFluidVariant();
        renderFluid(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, fluidStack, pBlockEntity.getFluidStorage().getAmount());
    }

    public static void renderFluid(MatrixStack pPoseStack, VertexConsumerProvider pBufferSource, int pPackedLight, int pPackedOverlay, FluidVariant fluidVariant, long amount){
        if (fluidVariant.isBlank()) return;

        int color = FluidVariantRendering.getColor(fluidVariant);
        Sprite sprite = FluidVariantRendering.getSprites(fluidVariant)[0];
        RenderLayer renderLayer = RenderLayer.getEntityTranslucent(sprite.getAtlasId());

        float height = ((float) amount / (FluidConstants.BUCKET * 50)) * 0.90f;
        height += 0.05f;

        VertexConsumer builder = pBufferSource.getBuffer(renderLayer);

        drawQuad(builder, pPoseStack, 0.2f, height, 0.2f, 0.80f, height, 0.80f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color, pPackedOverlay);

        drawQuad(builder, pPoseStack, 0.2f, 0.05f, 0.2f, 0.80f, height, 0.2f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color, pPackedOverlay);
        pPoseStack.push();
        pPoseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        pPoseStack.translate(-1f, 0, -1.6f);
        drawQuad(builder, pPoseStack, 0.2f, 0.05f, 0.80f, 0.80f, height, 0.80f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color, pPackedOverlay);
        pPoseStack.pop();
        pPoseStack.push();
        pPoseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        pPoseStack.translate(-1f, 0, 0);
        drawQuad(builder, pPoseStack, 0.2f, 0.05f, 0.2f, 0.80f, height, 0.2f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color, pPackedOverlay);
        pPoseStack.pop();
        pPoseStack.push();
        pPoseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
        pPoseStack.translate(0, 0, -1f);
        drawQuad(builder, pPoseStack, 0.2f, 0.05f, 0.2f, 0.80f, height, 0.2f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), pPackedLight, color, pPackedOverlay);
        pPoseStack.pop();
    }

    private static void drawVertex(VertexConsumer builder, MatrixStack poseStack, float x, float y, float z, float u, float v, int packedLight, int color, int overlay) {
        builder.vertex(poseStack.peek().getPositionMatrix(), x, y, z)
                .color(color)
                .texture(u, v)
                .light(packedLight)
                .overlay(overlay)
                .normal(1, 0, 0);
    }
    private static void drawQuad(VertexConsumer builder, MatrixStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int packedLight, int color, int overlay) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, color, overlay);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, color, overlay);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, color, overlay);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, color, overlay);
    }
}
