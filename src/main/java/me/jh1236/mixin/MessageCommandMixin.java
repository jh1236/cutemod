package me.jh1236.mixin;

import net.minecraft.server.command.MessageCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(method = "register", at = @At(value = "HEAD"), cancellable = true)
    private static void injected(CallbackInfo ci) {
        ci.cancel();
    }
}