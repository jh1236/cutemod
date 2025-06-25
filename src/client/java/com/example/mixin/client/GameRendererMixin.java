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

    CustomKeybinds keybinds = new CustomKeybinds();

    @Inject(at=@At("HEAD"), method="getFov", cancellable = true)
    void FOVInject(CallbackInfoReturnable<Float> cir) {
        keybinds.setClientOptions(MinecraftClient.getInstance().options);
        keybinds.zoom();
        cir.setReturnValue(keybinds.getZoom());
    }
}
