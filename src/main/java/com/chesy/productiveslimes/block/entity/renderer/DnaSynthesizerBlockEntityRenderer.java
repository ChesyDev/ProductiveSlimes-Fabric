package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.DnaSynthesizerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class DnaSynthesizerBlockEntityRenderer implements BlockEntityRenderer<DnaSynthesizerBlockEntity> {
    public DnaSynthesizerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(DnaSynthesizerBlockEntity pBlockEntity, float tickDelta, MatrixStack pPoseStack, VertexConsumerProvider pBufferSource, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        pPoseStack.push();

        Direction facing = pBlockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
        
        ItemStack input1 = pBlockEntity.getItems().get(0);
        ItemStack input2 = pBlockEntity.getItems().get(1);
        ItemStack input3 = pBlockEntity.getItems().get(2);
        ItemStack output = pBlockEntity.getItems().get(4);

        switch (facing) {
            case EAST:
                // Render the input slots
                renderItem(input1, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0.3f, 0, 0.25f);
                renderItem(input2, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, -0.3f, 0, 0.25f);
                renderItem(input3, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0, 0, 0.125f);

                // Render the output slot
                renderItem(output, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0, 0, -0.25f);
                break;
            case WEST:
                // Render the input slots
                renderItem(input1, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, -0.3f, 0, -0.25f);
                renderItem(input2, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0.3f, 0, -0.25f);
                renderItem(input3, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0, 0, -0.125f);

                // Render the output slot
                renderItem(output, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0, 0, 0.25f);
                break;
            case NORTH:
                // Render the input slots
                renderItem(input1, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0.25f, 0, 0.3f);
                renderItem(input2, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0.25f, 0, -0.3f);
                renderItem(input3, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0.125f, 0, 0);

                // Render the output slot
                renderItem(output, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, -0.25f, 0, 0);
                break;
            case SOUTH:
                // Render the input slots
                renderItem(input1, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, -0.25f, 0, -0.3f);
                renderItem(input2, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, -0.25f, 0, 0.3f);
                renderItem(input3, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, -0.125f, 0, 0);

                // Render the output slot
                renderItem(output, pPoseStack, pBufferSource, pBlockEntity, itemRenderer, 0.25f, 0, 0f);
                break;

        }

        pPoseStack.pop();
    }

    private void renderItem(ItemStack itemStack, MatrixStack pPoseStack, VertexConsumerProvider pBufferSource, DnaSynthesizerBlockEntity pBlockEntity, ItemRenderer itemRenderer, float xOffset, float yOffset, float zOffset) {
        pPoseStack.push();
        pPoseStack.translate(0.5 + xOffset, 0.35 + yOffset, 0.5 + zOffset);
        pPoseStack.scale(0.25f, 0.25f, 0.25f);
        pPoseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(pBlockEntity.getRenderingRotation()));

        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, 0xF000F0, OverlayTexture.DEFAULT_UV, pPoseStack, pBufferSource, pBlockEntity.getWorld(), 1);

        pPoseStack.pop();
    }
}
