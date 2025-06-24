package com.example;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "modid";
    private static Random random = new Random();
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public final HashMap<String, ViewingTypes> freaks = new HashMap<>();
    public final HashMap<String, PlayerEntity> convo = new HashMap<>();

    public static ItemStack CustomBundle = Items.BLACK_BUNDLE.getDefaultStack();

    static {
        NbtCompound nbt = new NbtCompound();
        nbt.put("graves", NbtByte.ONE);
        CustomBundle.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public enum ViewingTypes {
        HIDDEN,
        SCRAMBLED,
        HOVER,
        SHOWN
    }

    @Override
    public void onInitialize() {
        ServerPlayerEvents.JOIN.register(this::onJoin);

        final Style whisper = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY)).withItalic(true);
        final Style spinnyText = Style.EMPTY.withObfuscated(true)
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("run /nsfw shown to see this message!")))
                .withClickEvent(new ClickEvent.SuggestCommand("/nsfw shown"));
        final Style hideText = Style.EMPTY
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("run /nsfw hidden to get hide this message!")))
                .withClickEvent(new ClickEvent.SuggestCommand("/nsfw hidden")).withColor(Formatting.YELLOW);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("nsfw")
                    .then(argument("value", StringArgumentType.greedyString())
                            .executes(context -> {
                                final String value = StringArgumentType.getString(context, "value");
                                var player = context.getSource().getPlayer();
                                var playerName = player.getNameForScoreboard();
                                var senderFreakiness = freaks.getOrDefault(playerName, ViewingTypes.HIDDEN);
                                if (senderFreakiness != ViewingTypes.SHOWN && senderFreakiness != ViewingTypes.HOVER) {
                                    freaks.put(playerName, ViewingTypes.SHOWN);
                                    context.getSource().sendFeedback(() -> Text.literal("You were not registered as nsfw, so we added you to the freakshow!").setStyle(whisper), false);
                                }
                                MinecraftServer server = player.getServer();
                                for (var playerEntity : server.getPlayerManager().getPlayerList()) {
                                    String name = playerEntity.getNameForScoreboard();
                                    switch (freaks.getOrDefault(name, ViewingTypes.SCRAMBLED)) {
                                        case HIDDEN -> {
                                            // No need to send a message if they keep chat hidden
                                        }
                                        case ViewingTypes.SCRAMBLED -> {
                                            playerEntity.sendMessage(Text.literal("<" + player.getNameForScoreboard() + " (freakily)> ").append(Text.literal(value).setStyle(spinnyText)).append(Text.literal(" [Hide]").setStyle(hideText)), false);
                                        }
                                        case ViewingTypes.HOVER -> {
                                            final Style hoverText = Style.EMPTY.withObfuscated(true)
                                                    .withHoverEvent(new HoverEvent.ShowText(Text.literal(value)));
                                            playerEntity.sendMessage(Text.literal("<" + player.getNameForScoreboard() + " (freakily)> ").append(Text.literal(value).setStyle(hoverText)), false);
                                        }
                                        case ViewingTypes.SHOWN -> {
                                            playerEntity.sendMessage(Text.literal("<" + player.getNameForScoreboard() + " (freakily)> " + value), false);
                                        }
                                    }
                                }

                                return 1;
                            })));
            dispatcher.register(literal("nsfw")
                    .then(literal("hidden").executes(context -> {
                        final ServerCommandSource source = context.getSource();
                        if (!source.isExecutedByPlayer()) return 0;
                        final String self = source.getPlayer().getNameForScoreboard();
                        freaks.put(self, ViewingTypes.HIDDEN);
                        context.getSource().sendFeedback(() -> Text.literal("We get it. Waiting till marriage. You have hidden the NSFW chat."), false);
                        return 1;
                    })));
            dispatcher.register(literal("nsfw")
                    .then(literal("scrambled").executes(context -> {
                        final ServerCommandSource source = context.getSource();
                        if (!source.isExecutedByPlayer()) return 0;
                        final String self = source.getPlayer().getNameForScoreboard();
                        freaks.put(self, ViewingTypes.SCRAMBLED);
                        context.getSource().sendFeedback(() -> Text.literal("So does the mystery add to the fun or...  The NSFW Chat will be scrambled."), false);
                        return 1;
                    })));
            dispatcher.register(literal("nsfw")
                    .then(literal("shown").executes(context -> {
                        final ServerCommandSource source = context.getSource();
                        if (!source.isExecutedByPlayer()) return 0;
                        final String self = source.getPlayer().getNameForScoreboard();
                        freaks.put(self, ViewingTypes.SHOWN);
                        context.getSource().sendFeedback(() -> Text.literal("You little pervert! You have been added to the NSFW chat."), false);
                        return 1;
                    })));
            dispatcher.register(literal("nsfw")
                    .then(literal("hover").executes(context -> {
                        final ServerCommandSource source = context.getSource();
                        if (!source.isExecutedByPlayer()) return 0;
                        final String self = source.getPlayer().getNameForScoreboard();
                        freaks.put(self, ViewingTypes.HOVER);
                        context.getSource().sendFeedback(() -> Text.literal("So you can only take it in small doses huh? The NSFW chat will be visible on hover."), false);
                        return 1;
                    })));
            final LiteralCommandNode<ServerCommandSource> message = dispatcher.register(literal("w")
                    .then(argument("player", EntityArgumentType.player()).then(argument("msg", StringArgumentType.greedyString()).executes(context -> {
                        final var otherPlayer = EntityArgumentType.getPlayer(context, "player");
                        final var me = context.getSource().getPlayer();
                        final var msg = StringArgumentType.getString(context, "msg");
                        otherPlayer.sendMessage(Text.literal(me.getNameForScoreboard() + " whispers to you: " + msg).setStyle(whisper), false);
                        convo.put(me.getNameForScoreboard(), otherPlayer);
                        convo.put(otherPlayer.getNameForScoreboard(), me);
                        context.getSource().sendFeedback(() -> Text.literal("You whisper to " + otherPlayer.getNameForScoreboard() + ": " + msg).setStyle(whisper), false);
                        return 1;
                    }))));
            dispatcher.register(literal("msg").redirect(message));
            dispatcher.register(literal("tell").redirect(message));
            dispatcher.register(literal("r").then(argument("msg", StringArgumentType.greedyString()).executes((context) -> {
                final var me = context.getSource().getPlayer().getNameForScoreboard();
                final var otherPlayer = convo.get(me);
                final var msg = StringArgumentType.getString(context, "msg");
                otherPlayer.sendMessage(Text.literal(me + " whispers to you: " + msg).setStyle(whisper), false);
                context.getSource().sendFeedback(() -> Text.literal("You whisper to " + otherPlayer.getNameForScoreboard() + ": " + msg).setStyle(whisper), false);
                return 1;
            })));
            dispatcher.register(literal("impersonate")
                    .then(argument("player", StringArgumentType.string()).then(argument("msg", StringArgumentType.greedyString()).executes(context -> {
                        final var otherPlayer = StringArgumentType.getString(context, "player");
                        final var msg = StringArgumentType.getString(context, "msg");
                        context.getSource().getPlayer().getServer().getPlayerManager().broadcast(Text.literal("<" + otherPlayer + "> " + msg), false);
                        return 1;
                    }))));
        });
    }

    private void onJoin(ServerPlayerEntity serverPlayerEntity) {
        MinecraftServer server = serverPlayerEntity.getServer();
        PlayerManager playerManager = server.getPlayerManager();
        if (playerManager.getPlayerList().stream().noneMatch(p -> p.getNameForScoreboard().equals("Jh1236"))) {
            new Thread(() -> threadedJoinMessage(playerManager)).start();
        }
    }

    private void threadedJoinMessage(PlayerManager playerManager) {
        try {
            Thread.sleep((long) random.nextDouble(2.0, 15.0) * 1000);
            playerManager.broadcast(Text.literal("Jh1236 joined the game").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), false);
            Thread.sleep((long) random.nextDouble(1.0, 4.0) * 1000);
            playerManager.broadcast(Text.literal("<Jh1236> o/"), false);
            if (random.nextDouble() > 0.8) {
                Thread.sleep((long) random.nextDouble(10.0, 60.0) * 1000);
                playerManager.broadcast(Text.literal("<Jh1236> wyd?"), false);
            }
            Thread.sleep((long) random.nextDouble(10.0, 40.0) * 1000);
            playerManager.broadcast(Text.literal("Jh1236 left the game").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}