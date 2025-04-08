package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.DnaExtractorBlockEntity;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class DnaExtractorBlockEntityRenderer implements BlockEntityRenderer<DnaExtractorBlockEntity> {
    public DnaExtractorBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(DnaExtractorBlockEntity pBlockEntity, float tickDelta, MatrixStack pPoseStack, VertexConsumerProvider pBufferSource, int light, int overlay, Vec3d cameraPos) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = pBlockEntity.getRenderStack();

        pPoseStack.push();
        if (itemStack.getItem() instanceof SlimeballItem){
            pPoseStack.translate(0.5, 0.4, 0.5);
        }
        else{
            pPoseStack.translate(0.5, 0.5, 0.5);
        }
        pPoseStack.scale(0.35f, 0.35f, 0.35f);
        pPoseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(pBlockEntity.getRenderingRotation()));

        itemRenderer.renderItem(itemStack, ItemDisplayContext.FIXED, 0xF000F0, OverlayTexture.DEFAULT_UV, pPoseStack, pBufferSource, pBlockEntity.getWorld(), 1);

        pPoseStack.pop();
    }
}