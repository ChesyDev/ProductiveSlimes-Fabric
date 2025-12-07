package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.SlimeNestBlockEntity;
import com.chesy.productiveslimes.block.entity.renderstate.SlimeNestBlockEntityRenderState;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.entity.model.BaseSlimeModel;
import com.chesy.productiveslimes.entity.renderer.BaseSlimeRenderer;
import com.chesy.productiveslimes.util.SlimeData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SlimeNestBlockEntityRenderer implements BlockEntityRenderer<SlimeNestBlockEntity, SlimeNestBlockEntityRenderState> {
    public int tick;
    private final ItemModelManager itemModelResolver;
    private final LoadedEntityModels entityModelSet;
    private final BaseSlimeModel slimeModel;
    private final BaseSlimeModel slimeModelOuter;

    public SlimeNestBlockEntityRenderer(BlockEntityRendererFactory.Context context){
        this.itemModelResolver = context.itemModelManager();
        this.entityModelSet = context.loadedEntityModels();
        this.slimeModel = new BaseSlimeModel(entityModelSet.getModelPart(EntityModelLayers.SLIME), -1);
        this.slimeModelOuter = new BaseSlimeModel(entityModelSet.getModelPart(EntityModelLayers.SLIME_OUTER), -1);
    }

    @Override
    public void updateRenderState(SlimeNestBlockEntity blockEntity, SlimeNestBlockEntityRenderState renderState, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, renderState, tickProgress, cameraPos, crumblingOverlay);
        renderState.blockEntity = blockEntity;
        SlimeData slimeData = blockEntity.getStack(blockEntity.slimeSlot[0]).get(ModDataComponents.SLIME_DATA);
        if (slimeData != null){
            renderState.slimeColor = slimeData.color();
        } else {
            renderState.slimeColor = -1; // default
        }

        ItemRenderState itemStackRenderState = new ItemRenderState();
        this.itemModelResolver.clearAndUpdate(itemStackRenderState, blockEntity.getSlime(), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 0);
        renderState.itemStackRenderState = itemStackRenderState;
        renderState.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
    }

    @Override
    public void render(SlimeNestBlockEntityRenderState renderState, MatrixStack poseStack, OrderedRenderCommandQueue nodeCollector, CameraRenderState p_451022_) {
        SlimeNestBlockEntity blockEntity = renderState.blockEntity;
        if (blockEntity.getSlime() == null) return;
        if (blockEntity.getSlime().isEmpty()) return;

        if (!blockEntity.getSlime().contains(ModDataComponents.SLIME_DATA)) return;
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack slime = blockEntity.getSlime();
        World level = blockEntity.getWorld();

        // Get the center of the block
        double centerX = blockEntity.getPos().getX() + 0.5;
        double centerY = blockEntity.getPos().getY() + 0.5;
        double centerZ = blockEntity.getPos().getZ() + 0.5;

        // Ensure level is not null and is client-side
        if (level == null || !level.isClient()) return;
        tick = blockEntity.getData().get(4);

        // Calculate squishAmount based on tickCount
        float squishAmount = 1.0F + 0.1F * (float) Math.sin(tick * 0.1F);

        // Vertical bounce sync with squish
        float bounce = 0.1F * (float) Math.sin(tick * 0.1F);

        // Adjust the scale and position based on squish
        float scaleX = squishAmount;
        float scaleY = 1.0F / squishAmount; // Opposite squish for a "flattening" effect
        float scaleZ = squishAmount;

        // Center the rendered item
        float renderX = 0.0F; // Offset in the pose stack to remain centered
        float renderY = 0.0F + bounce; // Apply vertical bounce
        float renderZ = 0.0F;

        Direction direction = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
        float degree = direction.getPositiveHorizontalDegrees();
        // Render the squishing slime at the center of the block
        poseStack.push();
        poseStack.translate(centerX - blockEntity.getPos().getX() + renderX, 1.7, centerZ - blockEntity.getPos().getZ() + renderZ);
        poseStack.scale(scaleX, scaleY, scaleZ); // Apply squish scaling
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degree));
        nodeCollector.getBatchingQueue(0).submitModel(this.slimeModel, new SlimeEntityRenderState(), poseStack, RenderLayers.entityTranslucent(BaseSlimeRenderer.TEXTURE), renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, renderState.slimeColor, null, 0, null);
        nodeCollector.getBatchingQueue(1).submitModel(this.slimeModelOuter, new SlimeEntityRenderState(), poseStack, RenderLayers.entityTranslucent(BaseSlimeRenderer.TEXTURE), renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, renderState.slimeColor, null, 0, null);
        poseStack.pop();
    }

    @Override
    public SlimeNestBlockEntityRenderState createRenderState() {
        return new SlimeNestBlockEntityRenderState();
    }
}
