package com.example;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "modid";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public final HashMap<String, Integer> freaks = new HashMap<>();
    public final HashMap<String, PlayerEntity> convo = new HashMap<>();


    @Override

    public void onInitialize() {
        final Style whisper = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY)).withItalic(true);
        final Style spinnyText = Style.EMPTY.withObfuscated(true)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("run /nsfw shown to see this message!")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nsfw shown"));
        final Style hideText = Style.EMPTY
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("run /nsfw hidden to get hide this message!")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nsfw hidden")).withColor(Formatting.YELLOW);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("nsfw")
                    .then(argument("value", StringArgumentType.greedyString())
                            .executes(context -> {
                                final String value = StringArgumentType.getString(context, "value");
                                var player = context.getSource().getPlayer();
                                var playerName = player.getNameForScoreboard();
                                if (!freaks.containsKey(playerName)) {
                                    freaks.put(playerName, 2);
                                    context.getSource().sendFeedback(() -> Text.literal("You were not registered as nsfw, so we added you to the freakshow!").setStyle(whisper), false);
                                }
                                MinecraftServer server = player.getServer();
                                for (var playerEntity : server.getPlayerManager().getPlayerList()) {
                                    String name = playerEntity.getNameForScoreboard();
                                    switch (freaks.getOrDefault(name, 1)) {
                                        case 1 -> {
                                            playerEntity.sendMessage(Text.literal("<" + player.getNameForScoreboard() + " (freakily)> ").append(Text.literal(value).setStyle(spinnyText)).append(Text.literal(" [Hide]").setStyle(hideText)), false);
                                        }
                                        case 2 -> {
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
                        freaks.put(self, 0);
                        context.getSource().sendFeedback(() -> Text.literal("We get it. Waiting till marriage. You have hidden the NSFW chat."), false);
                        return 1;
                    })));
            dispatcher.register(literal("nsfw")
                    .then(literal("scrambled").executes(context -> {
                        final ServerCommandSource source = context.getSource();
                        if (!source.isExecutedByPlayer()) return 0;
                        final String self = source.getPlayer().getNameForScoreboard();
                        freaks.put(self, 1);
                        context.getSource().sendFeedback(() -> Text.literal("So does the mystery add to the fun or...  The NSFW Chat will be scrambled."), false);
                        return 1;
                    })));
            dispatcher.register(literal("nsfw")
                    .then(literal("shown").executes(context -> {
                        final ServerCommandSource source = context.getSource();
                        if (!source.isExecutedByPlayer()) return 0;
                        final String self = source.getPlayer().getNameForScoreboard();
                        freaks.put(self, 2);
                        context.getSource().sendFeedback(() -> Text.literal("You little pervert! You have been added to the NSFW chat."), false);
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
        });


        LOGGER.info("Hello Fabric world!");
    }
}