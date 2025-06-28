package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Random;

public class ClientMessageManager implements ClientModInitializer {
    private final ArrayList<String> advancements = new ArrayList<>();
    private boolean waves = true;
    private boolean ggs = true;

    private final Random random = new Random();

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("toggle_waves").executes(context -> {
                waves = !waves;
                context.getSource().sendFeedback(Text.literal("Wave on join toggled! Waving on join is " + (waves ? "" : "not ") + "enabled"));
                return 1;
            }));
            dispatcher.register(ClientCommandManager.literal("toggle_ggs").executes(context -> {
                ggs = !ggs;
                context.getSource().sendFeedback(Text.literal("Advancement GG toggled! GG on advancement is " + (ggs ? "" : "not ") + "enabled"));
                return 1;
            }));
        });
        ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> this.onChat(text));
        ClientReceiveMessageEvents.GAME.register((text, b) -> this.onChat(text));

        advancements.add(" has made the advancement");
        advancements.add(" has completed the challenge");
        advancements.add(" has reached the goal");
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            if (minecraftClient.player != null && waves) {
                sendMessageWithRandomDelay(minecraftClient.player, "o/", 300, 2000);
            }
        });
    }

    private void onChat(Text text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
        String message = text.getString();
        if (message.startsWith("<") || message.contains(player.getName().getString())) {
            return;
        }
        if (ggs) {

            for (var i : advancements) {
                if (message.contains(i)) {
                    sendMessageWithRandomDelay(player, "gg", 300, 2000);
                    return;
                }
            }
        }
        if (message.contains(" joined the game") && waves) {
            sendMessageWithRandomDelay(player, "o/", 300, 2000);
        }
    }


    private void sendMessageWithRandomDelay(ClientPlayerEntity player, String message, int minDelay, int maxDelay) {
        new Thread(() -> {
            try {
                Thread.sleep(random.nextInt(minDelay, maxDelay));
                player.networkHandler.sendChatMessage(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}