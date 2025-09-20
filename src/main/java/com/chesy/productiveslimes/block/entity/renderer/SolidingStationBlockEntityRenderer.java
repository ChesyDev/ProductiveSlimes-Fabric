package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.SolidingStationBlockEntity;
import com.chesy.productiveslimes.block.entity.renderstate.SolidingStationBlockEntityRenderState;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SolidingStationBlockEntityRenderer implements BlockEntityRenderer<SolidingStationBlockEntity, SolidingStationBlockEntityRenderState> {
    public SolidingStationBlockEntityRenderer(BlockEntityRendererFactory.Context pContext) {

    }

    @Override
    public void updateRenderState(SolidingStationBlockEntity blockEntity, SolidingStationBlockEntityRenderState renderState, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, renderState, tickProgress, cameraPos, crumblingOverlay);
        renderState.blockEntity = blockEntity;
        renderState.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
    }

    @Override
    public void render(SolidingStationBlockEntityRenderState renderState, MatrixStack poseStack, OrderedRenderCommandQueue nodeCollector, CameraRenderState p_451022_) {
        SolidingStationBlockEntity blockEntity = renderState.blockEntity;
        FluidVariant fluidStack = blockEntity.getRenderStack();
        if (fluidStack.isBlank()) return;

        int color = FluidVariantRendering.getColor(fluidStack);
        Sprite sprite = FluidVariantRendering.getSprites(fluidStack)[0];
        RenderLayer renderLayer = RenderLayer.getEntityTranslucent(sprite.getAtlasId());

        float height = 0.8f;

        nodeCollector.submitCustom(poseStack, renderLayer, (pose, builder) -> {
            drawQuad(builder, pose, 0.2f, height, 0.2f, 0.80f, height, 0.2f, 0.80f, height, 0.80f, 0.2f, height, 0.80f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), renderState.lightmapCoordinates, color, 0, -1, 0);

            drawQuad(builder, pose, 0.2f, 0.05f, 0.2f, 0.80f, height, 0.2f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), renderState.lightmapCoordinates, color, 0, 0, -1);
            poseStack.push();
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            poseStack.translate(-1f, 0, -1.6f);
            drawQuad(builder, pose, 0.8f, 0.05f, 0.80f, 0.2f, height, 0.80f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), renderState.lightmapCoordinates, color, 0, 0, 1);
            poseStack.pop();
            poseStack.push();
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            poseStack.translate(-1f, 0, 0);
            drawQuad(builder, pose, 0.2f, 0.05f, 0.2f, 0.20f, height, 0.8f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), renderState.lightmapCoordinates, color, -1, 0, 0);
            poseStack.pop();
            poseStack.push();
            poseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
            poseStack.translate(0, 0, -1f);
            drawQuad(builder, pose, 0.8f, 0.05f, 0.2f, 0.80f, height, 0.8f, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), renderState.lightmapCoordinates, color, 1, 0, 0);
            poseStack.pop();
        });
    }

    private static void drawVertex(VertexConsumer builder, MatrixStack.Entry pose,
                                   float x, float y, float z,
                                   float u, float v,
                                   int packedLight, int color,
                                   float nx, float ny, float nz) {
        builder.vertex(pose.getPositionMatrix(), x, y, z)
                .color(color)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(packedLight)
                .normal(pose, nx, ny, nz);
    }

    private static void drawQuad(VertexConsumer builder, MatrixStack.Entry pose,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1,
                                 float u0, float v0, float u1, float v1,
                                 int packedLight, int color,
                                 float nx, float ny, float nz) {
        drawVertex(builder, pose, x0, y0, z0, u0, v0, packedLight, color, nx, ny, nz);
        drawVertex(builder, pose, x0, y1, z0, u0, v1, packedLight, color, nx, ny, nz);
        drawVertex(builder, pose, x1, y1, z1, u1, v1, packedLight, color, nx, ny, nz);
        drawVertex(builder, pose, x1, y0, z1, u1, v0, packedLight, color, nx, ny, nz);
    }

    private static void drawQuad(VertexConsumer builder, MatrixStack.Entry pose,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3,
                                 float u0, float v0, float u1, float v1,
                                 int packedLight, int color,
                                 float nx, float ny, float nz) {
        drawVertex(builder, pose, x0, y0, z0, u0, v0, packedLight, color, nx, ny, nz);
        drawVertex(builder, pose, x1, y1, z1, u0, v1, packedLight, color, nx, ny, nz);
        drawVertex(builder, pose, x2, y2, z2, u1, v1, packedLight, color, nx, ny, nz);
        drawVertex(builder, pose, x3, y3, z3, u1, v0, packedLight, color, nx, ny, nz);
    }

    @Override
    public SolidingStationBlockEntityRenderState createRenderState() {
        return new SolidingStationBlockEntityRenderState();
    }
}
