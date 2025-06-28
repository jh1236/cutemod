package com.example.mixin.client;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "getSwappableHotbarSlot", at = @At("HEAD"), cancellable = true)
    public void inject(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(8);
    }
}