package com.chesy.productiveslimes.block.entity.renderer;

import com.chesy.productiveslimes.block.entity.SlimeNestBlockEntity;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SlimeNestBlockEntityRenderer implements BlockEntityRenderer<SlimeNestBlockEntity> {
    public int tick;

    public SlimeNestBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    @Override
    public void render(SlimeNestBlockEntity blockEntity, float tickDelta, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
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
        if (level == null || !level.isClient) return;
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

        // Spawn particles during the squish
        /*if (tick % 20 == 0) {
            for (int i = 0; i < 5; i++) { // Spawn 5 particles
                double offsetX = level.random.nextDouble() * 0.4 - 0.2; // Random offset around the X-axis
                double offsetY = level.random.nextDouble() * 0.3;       // Random offset upward
                double offsetZ = level.random.nextDouble() * 0.4 - 0.2; // Random offset around the Z-axis
                // Add the particle with a randomized position surrounding the slime
                level.addParticle(
                        new ItemStackParticleEffect(ParticleTypes.ITEM,
                                new ItemStack(slime.get(ModDataComponents.SLIME_DATA).growthItem().getItem())),
                        centerX + offsetX,
                        centerY + offsetY,
                        centerZ + offsetZ,
                        0.0, 0.1, 0.0
                );
            }
        }*/
        Direction direction = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
        int degree = 0;
        switch (direction) {
            case NORTH:
                degree = 0;
                break;
            case EAST:
                degree = 270;
                break;
            case SOUTH:
                degree = 180;
                break;
            case WEST:
                degree = 90;
                break;
        }
        // Render the squishing slime at the center of the block
        poseStack.push();
        poseStack.translate(centerX - blockEntity.getPos().getX() + renderX, centerY - blockEntity.getPos().getY() + renderY - 0.05f, centerZ - blockEntity.getPos().getZ() + renderZ);
        poseStack.scale(scaleX, scaleY, scaleZ); // Apply squish scaling
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degree));
        itemRenderer.renderItem(slime, ModelTransformationMode.FIXED, 0xF000F0, packedOverlay, poseStack, bufferSource, blockEntity.getWorld(), 1);
        poseStack.pop();
    }

    private int getLightLevel(World level, BlockPos pos) {
        int bLight = level.getLightLevel(LightType.BLOCK, pos);
        int sLight = level.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
