package me.jh1236.mixin.client;

import me.jh1236.ClientZoomManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"
            ), cancellable = true
    )
    public void inject(long window, double horizontal, double vertical, CallbackInfo ci, @Local int i) {
        if (ClientZoomManager.isZooming()) {
            ClientZoomManager.addToZoom(i);
            ci.cancel();
        }
    }
}
