package com.example.mixin.client;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.example.CustomKeybinds;


@Mixin(GameRenderer.class)
public class GameRendererMixin {
    final float clientFov = MinecraftClient.getInstance().options.getFov().getValue();
    final float zoomedFov = 30f;
    float zoomAmount = 0f;
    CustomKeybinds keybinds = new CustomKeybinds();

    @Unique
    float lerp(float a, float b, float t) {
        return ( a * (1f - t)) + (b * t);
    }

    @Unique
    float clamp01(float val) {
        if (val < 0) {return 0;}
        else if (val > 1) {return 1;}
        else {return val;}
    }

    @Inject(at=@At("HEAD"), method="getFov", cancellable = true)
    void FOVInject(CallbackInfoReturnable<Float> cir) {
        zoomAmount += keybinds.isZoomed() ? -0.1f : 0.1f;
        zoomAmount = clamp01(zoomAmount);
        cir.setReturnValue(lerp(zoomedFov, clientFov, zoomAmount));
    }
}
