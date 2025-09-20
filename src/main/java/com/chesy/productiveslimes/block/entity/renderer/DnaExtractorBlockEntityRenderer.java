package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.DnaExtractorBlockEntity;
import com.chesy.productiveslimes.block.entity.renderstate.DnaExtractorBlockEntityRenderState;
import com.chesy.productiveslimes.item.custom.SlimeballItem;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class DnaExtractorBlockEntityRenderer implements BlockEntityRenderer<DnaExtractorBlockEntity, DnaExtractorBlockEntityRenderState> {
    private final ItemModelManager itemModelManager;

    public DnaExtractorBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemModelManager = ctx.itemModelManager();
    }

    @Override
    public void updateRenderState(DnaExtractorBlockEntity blockEntity, DnaExtractorBlockEntityRenderState renderState, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, renderState, tickProgress, cameraPos, crumblingOverlay);
        renderState.blockEntity = blockEntity;
        renderState.rotation = renderState.getRenderingRotation();

        ItemRenderState itemstackrenderstate = new ItemRenderState();
        this.itemModelManager.clearAndUpdate(itemstackrenderstate, blockEntity.getRenderStack(), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 1);
        renderState.itemStackRenderState = itemstackrenderstate;
        renderState.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
    }

    @Override
    public void render(DnaExtractorBlockEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        DnaExtractorBlockEntity blockEntity = renderState.blockEntity;
        ItemStack itemStack = blockEntity.getRenderStack();

        matrices.push();
        if (itemStack.getItem() instanceof SlimeballItem){
            matrices.translate(0.5, 0.4, 0.5);
        }
        else{
            matrices.translate(0.5, 0.5, 0.5);
        }
        matrices.scale(0.35f, 0.35f, 0.35f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getRenderingRotation()));
        renderState.itemStackRenderState.render(matrices, queue, renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }

    @Override
    public DnaExtractorBlockEntityRenderState createRenderState() {
        return new DnaExtractorBlockEntityRenderState();
    }
}
