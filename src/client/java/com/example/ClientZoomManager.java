package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class ClientZoomManager implements ClientModInitializer {
    private static KeyBinding keyBinding;
    private static final boolean TOGGLE = false;
    private static final boolean MULTIPLY = false;
    private static int targetScrollCount = 1;
    private static float prevScrollCount = 1;
    private static boolean zooming = false;
    private static float zoomProgress = 0f;
    private static long lastFrame = 0L;


    private static GameOptions clientOptions = null;

    public static void addToZoom(int i) {
        prevScrollCount = getZoomCount();
        zoomProgress = 0f;
        if (MULTIPLY) {
            targetScrollCount += i;
        } else {
            if (i > 0) {
                targetScrollCount *= 2;
            } else {
                targetScrollCount /= 2;
            }
        }
        targetScrollCount = Math.max(1, targetScrollCount);
    }

    public static float getZoomCount() {
        prevScrollCount = MathHelper.lerp(zoomProgress, prevScrollCount, 1f * targetScrollCount);
        return prevScrollCount;
    }


    @Override
    public void onInitializeClient() {

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Zoom", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z, // The keycode of the key
                "CuteMod" // The translation key of the keybinding's category.
        ));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (clientOptions == null) {
                setClientOptions(client.options);
            }
            boolean change = false;
            if (TOGGLE) {
                while (keyBinding.wasPressed()) {
                    zooming = !zooming;
                    change = true;
                }
            } else {
                if (keyBinding.isPressed() != zooming) {
                    zooming = !zooming;
                    change = true;
                }
            }
            if (change) {
                prevScrollCount = getZoomCount();
                if (!zooming) {
                    targetScrollCount = 1;
                } else {
                    targetScrollCount = 3;
                }
                zoomProgress = 0f;
            }
            clientOptions.smoothCameraEnabled = zooming;
        });

    }

    public static void setClientOptions(GameOptions options) {
        clientOptions = options;
    }

    public static float clamp01(float val) {
        if (val < 0) {
            return 0;
        } else if (val > 1) {
            return 1;
        } else {
            return val;
        }
    }


    public static void zoom() {
        //working with 0.00 to change into seconds
        if (getZoomCount() != targetScrollCount) {
            zoomProgress += (0.001f) * (System.currentTimeMillis() - lastFrame);
            zoomProgress = clamp01(zoomProgress);
        }
        lastFrame = System.currentTimeMillis();
    }


    public static boolean isZooming() {
        return zooming;
    }
}
