package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.SlimeballCollectorBlockEntity;
import com.chesy.productiveslimes.block.entity.renderstate.SlimeballCollectorBlockEntityRenderState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SlimeballCollectorBlockEntityRenderer implements BlockEntityRenderer<SlimeballCollectorBlockEntity, SlimeballCollectorBlockEntityRenderState> {
    public SlimeballCollectorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void updateRenderState(SlimeballCollectorBlockEntity blockEntity, SlimeballCollectorBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        state.blockEntity = blockEntity;
        state.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
    }

    @Override
    public void render(SlimeballCollectorBlockEntityRenderState renderState, MatrixStack poseStack, OrderedRenderCommandQueue nodeCollector, CameraRenderState p_451022_) {
        SlimeballCollectorBlockEntity blockEntity = renderState.blockEntity;
        if (blockEntity.getWorld() == null) return;
        if (blockEntity.getData().get(0) == 0) return;
        // Define the collection area AABB (match this with your logic).
        int rangeXZ = 8; // Half of 16 blocks for X and Z.
        int rangeY = 256; // Full height.
        Box collectionArea = new Box(
                blockEntity.getPos().getX() - rangeXZ, -64, blockEntity.getPos().getZ() - rangeXZ,
                blockEntity.getPos().getX() + rangeXZ + 1, rangeY, blockEntity.getPos().getZ() + rangeXZ + 1
        );
        // Shift to world coordinates.
        poseStack.push();
        poseStack.translate(-blockEntity.getPos().getX(), -blockEntity.getPos().getY(), -blockEntity.getPos().getZ());
        // Render the outline box.
        renderOutline(poseStack, nodeCollector, collectionArea);
        poseStack.pop();
    }

    private void renderOutline(MatrixStack poseStack, OrderedRenderCommandQueue nodeCollector, Box aabb) {
        // Buffer for lines.
        RenderSystem.lineWidth(2.0f);
        // Render the outer box.
        nodeCollector.submitCustom(poseStack, RenderLayer.getLines(), (pose, vertexConsumer) -> {
            drawBox(pose, vertexConsumer, aabb, 1.0f, 0.0f, 0.0f, 1.0f); // Red color.
        });
        RenderSystem.lineWidth(1.0f);
    }

    private void drawBox(MatrixStack.Entry pose, VertexConsumer buffer, Box box, float red, float green, float blue, float alpha) {
        Matrix4f matrix = pose.getPositionMatrix();
        Matrix3f normal = pose.getNormalMatrix();
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;
        // Draw lines for the box.
        drawLine(matrix, buffer, x1, y1, z1, x2, y1, z1, red, green, blue, alpha);
        drawLine(matrix, buffer, x1, y1, z1, x1, y2, z1, red, green, blue, alpha);
        drawLine(matrix, buffer, x1, y1, z1, x1, y1, z2, red, green, blue, alpha);
        drawLine(matrix, buffer, x2, y2, z2, x1, y2, z2, red, green, blue, alpha);
        drawLine(matrix, buffer, x2, y2, z2, x2, y1, z2, red, green, blue, alpha);
        drawLine(matrix, buffer, x2, y2, z2, x2, y2, z1, red, green, blue, alpha);
        drawLine(matrix, buffer, x1, y2, z2, x1, y1, z2, red, green, blue, alpha);
        drawLine(matrix, buffer, x1, y2, z2, x2, y2, z2, red, green, blue, alpha);
        drawLine(matrix, buffer, x2, y1, z1, x2, y2, z1, red, green, blue, alpha);
        drawLine(matrix, buffer, x2, y1, z1, x1, y1, z1, red, green, blue, alpha);
        drawLine(matrix, buffer, x2, y1, z1, x2, y1, z2, red, green, blue, alpha);
        drawLine(matrix, buffer, x1, y2, z1, x2, y2, z1, red, green, blue, alpha);
        drawLine(matrix, buffer, x1, y2, z1, x1, y2, z2, red, green, blue, alpha);
    }

    private void drawLine(Matrix4f matrix, VertexConsumer buffer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        buffer.vertex(matrix, (float) x1, (float) y1, (float) z1).color(red, green, blue, alpha).texture(0, 0).light(0x00F000F0).overlay(OverlayTexture.DEFAULT_UV).normal(1, 0, 0);
        buffer.vertex(matrix, (float) x2, (float) y2, (float) z2).color(red, green, blue, alpha).texture(0, 0).light(0x00F000F0).overlay(OverlayTexture.DEFAULT_UV).normal(1, 0, 0);
    }

    @Override
    public SlimeballCollectorBlockEntityRenderState createRenderState() {
        return new SlimeballCollectorBlockEntityRenderState();
    }
}
