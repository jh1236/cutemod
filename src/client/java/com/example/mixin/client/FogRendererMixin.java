package com.example.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/MappableRingBuffer;getBlocking()Lcom/mojang/blaze3d/buffers/GpuBuffer;"))
    private FogData getFogBuffer(FogData value) {
        value.renderDistanceStart = value.renderDistanceEnd = value.environmentalStart = value.environmentalEnd = value.skyEnd = value.cloudEnd =
                MinecraftClient.getInstance().options.getClampedViewDistance() * 32;
        return value;
    }
}
