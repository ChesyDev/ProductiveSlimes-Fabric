package com.chesy.productiveslimes.command;

import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.datacomponent.custom.SlimeData;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.tier.ModTier;
import com.chesy.productiveslimes.tier.ModTiers;
import com.chesy.productiveslimes.tier.Tier;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("productiveslimes").requires(commandSourceStack -> commandSourceStack.hasPermissionLevel(2))
                .then(
                        CommandManager.literal("give").then(
                                CommandManager.argument("slime_id", StringArgumentType.string()).suggests((context, builder) -> {
                                                    List<String> ids = new ArrayList<>();
                                                    for (Tier tier : Tier.values()) {
                                                        ids.add(tier.getTierName());
                                                    }
                                                    return CommandSource.suggestMatching(ids, builder);
                                                }
                                        )
                                        .then(
                                                CommandManager.argument("size", IntegerArgumentType.integer(1, 4)).suggests((context, builder) -> {
                                                            List<String> sizes = new ArrayList<>();
                                                            sizes.add("1");
                                                            sizes.add("2");
                                                            sizes.add("3");
                                                            sizes.add("4");
                                                            return CommandSource.suggestMatching(sizes, builder);
                                                        })
                                                        .executes(ModCommands::execute)
                                        )
                        )
                )
        );
    }

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        // Ensure the command is executed by a player
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.translatable("command.productiveslimes.only_player_can_use"));
            return 0;
        }
        // Get arguments
        String slimeId = StringArgumentType.getString(context, "slime_id");
        int size = IntegerArgumentType.getInteger(context, "size");
        SlimeData data = createSlimeData(slimeId, size, source);
        if (data == null) {
            return 0;
        }
        // Create the item with custom NBT
        ItemStack slimeItem = new ItemStack(ModItems.SLIME_ITEM); // Replace with your mod's item
        slimeItem.set(ModDataComponents.SLIME_DATA, data);
        // Give the item to the player
        if (player.giveItemStack(slimeItem)) {
            source.sendFeedback(() -> Text.translatable("command.productiveslimes.item_gave"), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendError(Text.translatable("command.productiveslimes.item_not_gave"));
            return 0;
        }
    }

    private static SlimeData createSlimeData(String slimeId, int size, ServerCommandSource source) {
        try {
            ModTier tier = ModTiers.getTierByName(Tier.valueOf(slimeId.toUpperCase()));
            return new SlimeData(size, tier.color(), tier.cooldown(), new ItemStack(ModTiers.getSlimeballItemByName(slimeId)), new ItemStack(ModTiers.getItemByKey(tier.growthItemKey())), ModTiers.getEntityByName(slimeId));
        } catch (IllegalArgumentException e) {
            source.sendError(Text.translatable("command.productiveslimes.invalid_tier"));
            return null;
        }
    }
}
