package me.jh1236;

import me.jh1236.config.Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Random;

public class ClientMessageManager implements ClientModInitializer {
    private final ArrayList<String> advancements = new ArrayList<>();

    private final Random random = new Random();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(Config.class, Toml4jConfigSerializer::new);
        ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> this.onChat(text));
        ClientReceiveMessageEvents.GAME.register((text, b) -> this.onChat(text));

        advancements.add(" has made the advancement");
        advancements.add(" has completed the challenge");
        advancements.add(" has reached the goal");
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            if (minecraftClient.player != null && Config.readConfig().messages.joinMessageEnabled) {
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
        if (Config.readConfig().messages.advancementMessageEnabled) {

            for (var i : advancements) {
                if (message.contains(i)) {
                    sendMessageWithRandomDelay(player, "gg", 300, 2000);
                    return;
                }
            }
        }
        if (message.contains(" joined the game") && Config.readConfig().messages.joinMessageEnabled) {
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