package me.jh1236.mixin.client;


import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import me.jh1236.ClientZoomManager;


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
