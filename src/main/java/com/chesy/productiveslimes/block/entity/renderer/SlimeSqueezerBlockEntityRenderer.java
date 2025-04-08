package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.custom.SlimeSqueezerBlock;
import com.chesy.productiveslimes.block.entity.SlimeSqueezerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SlimeSqueezerBlockEntityRenderer implements BlockEntityRenderer<SlimeSqueezerBlockEntity> {
    public SlimeSqueezerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(SlimeSqueezerBlockEntity blockEntity, float tickDelta, MatrixStack poseStack, VertexConsumerProvider buffer, int light, int overlay, Vec3d cameraPos) {
        var squeezer = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModel(ModBlocks.SQUEEZER.getDefaultState());

        float progressRatio = (float) blockEntity.getData().get(0) / (float) blockEntity.getData().get(1);
        float startPoint = 0.8f;
        float endPoint = 0.15f;
        float squeezerPosition = startPoint - ((startPoint - endPoint) * progressRatio);
        float x1 = 0, x2 = 0, y1 = 0, y2 = 0, z1 = 0, z2 = 0;

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack inputItem = blockEntity.getInputStack();
        ItemStack outputItem1 = blockEntity.getOutputStack(0);
        ItemStack outputItem2 = blockEntity.getOutputStack(1);
        Direction facing = blockEntity.getCachedState().get(SlimeSqueezerBlock.FACING);

        // Render the squeezer
        poseStack.push();
        poseStack.translate(0, squeezerPosition, 0);
        renderModel(squeezer, poseStack, buffer, light, overlay);
        poseStack.pop();

        // Render the input item
        poseStack.push();
        poseStack.translate(0.5f, 0.09, 0.5f);
        poseStack.scale(0.35f, 0.35f, 0.35f);
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
        itemRenderer.renderItem(inputItem, ItemDisplayContext.FIXED, 0x0000D0, OverlayTexture.DEFAULT_UV, poseStack, buffer, blockEntity.getWorld(), 1);
        poseStack.pop();

        switch (facing){
            case SOUTH:
                x1 = 0.0625f; x2 = 0.9375f; y1 = 0.175f; y2 = 0.175f; z1 = 0.5f; z2 = 0.5f;
                break;
            case NORTH:
                x1 = 0.9375f; x2 = 0.0625f; y1 = 0.175f; y2 = 0.175f; z1 = 0.5f; z2 = 0.5f;
                break;
            case EAST:
                x1 = 0.5f; x2 = 0.5f; y1 = 0.175f; y2 = 0.175f; z1 = 0.9375f; z2 = 0.0625f;
                break;
            case WEST:
                x1 = 0.5f; x2 = 0.5f; y1 = 0.175f; y2 = 0.175f; z1 = 0.0625f; z2 = 0.9375f;
                break;
        }

        // Render the output item 1
        poseStack.push();
        poseStack.translate(x1, y1, z1);
        poseStack.scale(0.15f, 0.15f, 0.15f);
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
        itemRenderer.renderItem(outputItem1, ItemDisplayContext.FIXED, 0x0000D0, OverlayTexture.DEFAULT_UV, poseStack, buffer, blockEntity.getWorld(), 1);
        poseStack.pop();

        // Render the output item 2
        poseStack.push();
        poseStack.translate(x2, y2, z2);
        poseStack.scale(0.15f, 0.15f, 0.15f);
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
        itemRenderer.renderItem(outputItem2, ItemDisplayContext.FIXED, 0x0000D0, OverlayTexture.DEFAULT_UV, poseStack, buffer, blockEntity.getWorld(), 1);
        poseStack.pop();
    }

    private void renderModel(BlockStateModel model, MatrixStack poseStack, VertexConsumerProvider buffer, int light, int overlay) {
        Random rand = Random.create();

        for (Direction direction : Direction.values()) {
            rand.setSeed(42L);
            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                    poseStack.peek(),
                    buffer.getBuffer(RenderLayer.getCutout()),
                    model,
                    1.0F, 1.0F, 1.0F,
                    0x0000D0,
                    overlay
            );
        }
    }

    private int getLightLevel(World level, BlockPos pos) {
        int bLight = level.getLightLevel(LightType.BLOCK, pos);
        int sLight = level.getLightLevel(LightType.SKY, pos);

        return LightmapTextureManager.pack(bLight, sLight);
    }
}