package com.example.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.example.ClientZoomManager;


@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyVariable(
            method = "getFov",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;"
            ),
            index = 4 // Local variable slot for `f`; this may vary — see explanation below
    )
    private float modifyF(float f) {
        ClientZoomManager.zoom();
        return f / ClientZoomManager.getZoomCount(); // Apply your custom logic
    }
}
