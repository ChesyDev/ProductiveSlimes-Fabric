package com.chesy.productiveslimes.item.custom;

import com.chesy.productiveslimes.screen.custom.GuidebookMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuidebookItem extends Item {
    public GuidebookItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory() {

                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                    BlockPos pos = serverPlayerEntity.getBlockPos();
                    packetByteBuf.writeBlockPos(pos);
                }

                @Override
                public GuidebookMenu createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new GuidebookMenu(syncId, playerInventory);
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable("gui.productiveslimes.guidebook_menu");
                }
            });
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
