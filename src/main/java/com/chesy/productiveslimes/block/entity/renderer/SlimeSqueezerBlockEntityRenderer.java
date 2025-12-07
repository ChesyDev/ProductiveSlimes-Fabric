package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.custom.SlimeSqueezerBlock;
import com.chesy.productiveslimes.block.entity.SlimeSqueezerBlockEntity;
import com.chesy.productiveslimes.block.entity.renderstate.SlimeSqueezerBlockEntityRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimeSqueezerBlockEntityRenderer implements BlockEntityRenderer<SlimeSqueezerBlockEntity, SlimeSqueezerBlockEntityRenderState> {
    public ItemModelManager itemModelResolver;

    public SlimeSqueezerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelResolver = context.itemModelManager();
    }

    @Override
    public void updateRenderState(SlimeSqueezerBlockEntity blockEntity, SlimeSqueezerBlockEntityRenderState renderState, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, renderState, tickProgress, cameraPos, crumblingOverlay);
        renderState.blockEntity = blockEntity;

        List<ItemStack> itemStacks = List.of(
                blockEntity.getStack(blockEntity.inputSlots[0]),
                blockEntity.getStack(blockEntity.outputSlots[0]),
                blockEntity.getStack(blockEntity.outputSlots[1])
        );

        ItemRenderState itemStackRenderState = new ItemRenderState();
        ItemRenderState itemStackRenderState2 = new ItemRenderState();
        ItemRenderState itemStackRenderState3 = new ItemRenderState();

        this.itemModelResolver.clearAndUpdate(itemStackRenderState, itemStacks.get(0), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 1);
        this.itemModelResolver.clearAndUpdate(itemStackRenderState2, itemStacks.get(1), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 2);
        this.itemModelResolver.clearAndUpdate(itemStackRenderState3, itemStacks.get(2), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 3);

        renderState.itemStackRenderStates.add(itemStackRenderState);
        renderState.itemStackRenderStates.add(itemStackRenderState2);
        renderState.itemStackRenderStates.add(itemStackRenderState3);
        renderState.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
    }

    @Override
    public void render(SlimeSqueezerBlockEntityRenderState renderState, MatrixStack poseStack, OrderedRenderCommandQueue nodeCollector, CameraRenderState p_451022_) {
        SlimeSqueezerBlockEntity blockEntity = renderState.blockEntity;
        BlockStateModel squeezer = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModel(ModBlocks.SQUEEZER.getDefaultState());

        float progressRatio = (float) blockEntity.getData().get(0) / (float) blockEntity.getData().get(1);
        float startPoint = 0.8f;
        float endPoint = 0.15f;
        float squeezerPosition = startPoint - ((startPoint - endPoint) * progressRatio);
        float x1 = 0, x2 = 0, y1 = 0, y2 = 0, z1 = 0, z2 = 0;

        Direction facing = blockEntity.getCachedState().get(SlimeSqueezerBlock.FACING);

        // Render the squeezer
        poseStack.push();
        poseStack.translate(0, squeezerPosition, 0);
        renderModel(squeezer, poseStack, nodeCollector, renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV);
        poseStack.pop();

        // Render the input item
        poseStack.push();
        poseStack.translate(0.5f, 0.09, 0.5f);
        poseStack.scale(0.35f, 0.35f, 0.35f);
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
        renderState.itemStackRenderStates.get(0).render(poseStack, nodeCollector, renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
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
        renderState.itemStackRenderStates.get(1).render(poseStack, nodeCollector, renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
        poseStack.pop();

        // Render the output item 2
        poseStack.push();
        poseStack.translate(x2, y2, z2);
        poseStack.scale(0.15f, 0.15f, 0.15f);
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
        renderState.itemStackRenderStates.get(2).render(poseStack, nodeCollector, renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
        poseStack.pop();
    }

    private void renderModel(BlockStateModel model, MatrixStack poseStack, OrderedRenderCommandQueue nodeCollector, int light, int overlay) {
        Random rand = Random.create();

        for (Direction direction : Direction.values()) {
            rand.setSeed(42L);
            nodeCollector.submitBlockStateModel(poseStack, RenderLayers.cutout(), model, 1.0f, 1.0f, 1.0f, light, overlay, 0);
        }
    }

    @Override
    public SlimeSqueezerBlockEntityRenderState createRenderState() {
        return new SlimeSqueezerBlockEntityRenderState();
    }
}
