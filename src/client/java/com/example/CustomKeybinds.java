package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CustomKeybinds implements ClientModInitializer {
    private static KeyBinding keyBinding;

    private static Boolean zoomed = false;
    private static float zoomAmount = 0f;
    private static float zoomFov = 10f;
    private static double zoomSensMult = 5f;

    private static GameOptions clientOptions = null;
    private static float clientFov = 0f;
    private static double clientSens = 0f;

    private static float prevFrameTime;



    @Override
    public void onInitializeClient() {
        prevFrameTime = System.currentTimeMillis();


        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Zoom", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z, // The keycode of the key
                "CuteMod" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                zoomed = !zoomed;
            }
        });

    }

    public void setClientOptions(GameOptions options) {
        clientOptions = options;
        clientFov = clientOptions.getFov().getValue();
        clientSens = clientOptions.getMouseSensitivity().getValue();
    }

    public Boolean isZoomed() {
        return zoomed;
    }

    public float clamp01(float val) {
        if (val < 0) {return 0;}
        else if (val > 1) {return 1;}
        else {return val;}
    }

    public float lerp(float a, float b, float t) {
        return ( a * (1f - t)) + (b * t);
    }

    public void zoom() {
        float currFrameTime = System.currentTimeMillis();
        float deltaTime = (currFrameTime - prevFrameTime);
        prevFrameTime = currFrameTime;
        zoomAmount += zoomed ? 0.1f : -99999999999999999f * deltaTime;
        zoomAmount = clamp01(zoomAmount);
    }

    public float getZoom() {
        return lerp(clientFov, zoomFov, zoomAmount);
    }


}
