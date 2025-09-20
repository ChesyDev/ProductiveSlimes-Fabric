package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.DnaSynthesizerBlockEntity;
import com.chesy.productiveslimes.block.entity.renderstate.DnaSynthesizerBlockEntityRenderState;
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
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DnaSynthesizerBlockEntityRenderer implements BlockEntityRenderer<DnaSynthesizerBlockEntity, DnaSynthesizerBlockEntityRenderState> {
    private final ItemModelManager itemModelManager;

    public DnaSynthesizerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.itemModelManager();
    }

    @Override
    public void updateRenderState(DnaSynthesizerBlockEntity blockEntity, DnaSynthesizerBlockEntityRenderState renderState, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, renderState, tickProgress, cameraPos, crumblingOverlay);

        renderState.blockEntity = blockEntity;

        List<ItemStack> itemStacks = List.of(
                blockEntity.getStack(0),
                blockEntity.getStack(1),
                blockEntity.getStack(2),
                blockEntity.getStack(4)
        );

        ItemRenderState itemStackRenderState = new ItemRenderState();
        ItemRenderState itemStackRenderState2 = new ItemRenderState();
        ItemRenderState itemStackRenderState3 = new ItemRenderState();
        ItemRenderState itemStackRenderState4 = new ItemRenderState();

        this.itemModelManager.clearAndUpdate(itemStackRenderState, itemStacks.get(0), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 1);
        this.itemModelManager.clearAndUpdate(itemStackRenderState2, itemStacks.get(1), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 2);
        this.itemModelManager.clearAndUpdate(itemStackRenderState3, itemStacks.get(2), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 3);
        this.itemModelManager.clearAndUpdate(itemStackRenderState4, itemStacks.get(3), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 4);

        renderState.itemStackRenderStates.add(itemStackRenderState);
        renderState.itemStackRenderStates.add(itemStackRenderState2);
        renderState.itemStackRenderStates.add(itemStackRenderState3);
        renderState.itemStackRenderStates.add(itemStackRenderState4);
        renderState.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
    }

    @Override
    public void render(DnaSynthesizerBlockEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue nodeCollector, CameraRenderState cameraState) {
        matrices.push();

        Direction facing = renderState.blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);

        switch (facing) {
            case EAST:
                // Render the input slots
                renderItem(renderState.itemStackRenderStates.get(0), matrices, nodeCollector, renderState.blockEntity, renderState, 0.3f, 0, 0.25f);
                renderItem(renderState.itemStackRenderStates.get(1), matrices, nodeCollector, renderState.blockEntity, renderState, -0.3f, 0, 0.25f);
                renderItem(renderState.itemStackRenderStates.get(2), matrices, nodeCollector, renderState.blockEntity, renderState, 0, 0, 0.125f);

                // Render the output slot
                renderItem(renderState.itemStackRenderStates.get(3), matrices, nodeCollector, renderState.blockEntity, renderState, 0, 0, -0.25f);
                break;
            case WEST:
                // Render the input slots
                renderItem(renderState.itemStackRenderStates.get(0), matrices, nodeCollector, renderState.blockEntity, renderState, -0.3f, 0, -0.25f);
                renderItem(renderState.itemStackRenderStates.get(1), matrices, nodeCollector, renderState.blockEntity, renderState, 0.3f, 0, -0.25f);
                renderItem(renderState.itemStackRenderStates.get(2), matrices, nodeCollector, renderState.blockEntity, renderState, 0, 0, -0.125f);

                // Render the output slot
                renderItem(renderState.itemStackRenderStates.get(3), matrices, nodeCollector, renderState.blockEntity, renderState, 0, 0, 0.25f);
                break;
            case NORTH:
                // Render the input slots
                renderItem(renderState.itemStackRenderStates.get(0), matrices, nodeCollector, renderState.blockEntity, renderState, 0.25f, 0, 0.3f);
                renderItem(renderState.itemStackRenderStates.get(1), matrices, nodeCollector, renderState.blockEntity, renderState, 0.25f, 0, -0.3f);
                renderItem(renderState.itemStackRenderStates.get(2), matrices, nodeCollector, renderState.blockEntity, renderState, 0.125f, 0, 0);

                // Render the output slot
                renderItem(renderState.itemStackRenderStates.get(3), matrices, nodeCollector, renderState.blockEntity, renderState, -0.25f, 0, 0);
                break;
            case SOUTH:
                // Render the input slots
                renderItem(renderState.itemStackRenderStates.get(0), matrices, nodeCollector, renderState.blockEntity, renderState, -0.25f, 0, -0.3f);
                renderItem(renderState.itemStackRenderStates.get(1), matrices, nodeCollector, renderState.blockEntity, renderState, -0.25f, 0, 0.3f);
                renderItem(renderState.itemStackRenderStates.get(2), matrices, nodeCollector, renderState.blockEntity, renderState, -0.125f, 0, 0);

                // Render the output slot
                renderItem(renderState.itemStackRenderStates.get(3), matrices, nodeCollector, renderState.blockEntity, renderState, 0.25f, 0, 0f);
                break;

        }

        matrices.pop();
    }

    private void renderItem(ItemRenderState itemStackRenderState, MatrixStack pPoseStack, OrderedRenderCommandQueue queue, DnaSynthesizerBlockEntity pBlockEntity, DnaSynthesizerBlockEntityRenderState renderState, float xOffset, float yOffset, float zOffset) {
        pPoseStack.push();
        pPoseStack.translate(0.5 + xOffset, 0.35 + yOffset, 0.5 + zOffset);
        pPoseStack.scale(0.25f, 0.25f, 0.25f);
        pPoseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(pBlockEntity.getRenderingRotation()));

        itemStackRenderState.render(pPoseStack, queue, renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);

        pPoseStack.pop();
    }

    @Override
    public DnaSynthesizerBlockEntityRenderState createRenderState() {
        return new DnaSynthesizerBlockEntityRenderState();
    }
}
