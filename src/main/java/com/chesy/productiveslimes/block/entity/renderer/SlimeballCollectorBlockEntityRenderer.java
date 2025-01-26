package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.SlimeballCollectorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

public class SlimeballCollectorBlockEntityRenderer implements BlockEntityRenderer<SlimeballCollectorBlockEntity> {
    public SlimeballCollectorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(SlimeballCollectorBlockEntity blockEntity, float tickDelta, MatrixStack poseStack, VertexConsumerProvider bufferSource, int light, int overlay) {
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
        renderOutline(poseStack, bufferSource, collectionArea);
        poseStack.pop();
    }

    private void renderOutline(MatrixStack poseStack, VertexConsumerProvider bufferSource, Box aabb) {
        // Buffer for lines.
        var buffer = bufferSource.getBuffer(RenderLayer.of("glow_lines", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.LINES, 256,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(RenderPhase.LINES_PROGRAM)
                        .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2.0))) // Line width
                        .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                        .target(RenderPhase.ITEM_ENTITY_TARGET)
                        .cull(RenderPhase.DISABLE_CULLING) // Disable culling
                        .build(false)
        ));
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableCull();
        // Render the outer box.
        drawBox(poseStack, buffer, aabb, 1.0f, 0.0f, 0.0f, 1.0f); // Red color.
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableCull();
    }

    private void renderGrid(MatrixStack poseStack, VertexConsumer buffer, Box box, float red, float green, float blue, float alpha) {
        MatrixStack.Entry pose = poseStack.peek();
        Matrix4f matrix = pose.getPositionMatrix();
        // Chunk grid size (16 blocks).
        int chunkSize = 16;
        // Loop through the X and Z axes to draw the grid.
        for (double x = Math.ceil(box.minX / chunkSize) * chunkSize; x < box.maxX; x += chunkSize) {
            drawLine(matrix, buffer, x, box.minY, box.minZ, x, box.maxY, box.minZ, red, green, blue, alpha); // Vertical lines.
        }
        for (double z = Math.ceil(box.minZ / chunkSize) * chunkSize; z < box.maxZ; z += chunkSize) {
            drawLine(matrix, buffer, box.minX, box.minY, z, box.minX, box.maxY, z, red, green, blue, alpha); // Horizontal lines.
        }
    }

    private void drawBox(MatrixStack poseStack, VertexConsumer buffer, Box box, float red, float green, float blue, float alpha) {
        MatrixStack.Entry pose = poseStack.peek();
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
        buffer.vertex(matrix, (float) x1, (float) y1, (float) z1).color(red, green, blue, alpha).normal(1.0f, 0.0f, 0.0f);
        buffer.vertex(matrix, (float) x2, (float) y2, (float) z2).color(red, green, blue, alpha).normal(1.0f, 0.0f, 0.0f);
    }
}
