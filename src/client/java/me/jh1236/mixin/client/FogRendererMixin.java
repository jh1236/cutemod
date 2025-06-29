package me.jh1236.mixin.client;

import me.jh1236.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/MappableRingBuffer;getBlocking()Lcom/mojang/blaze3d/buffers/GpuBuffer;"))
    private FogData getFogBuffer(FogData value) {
        if (Config.readConfig().fogEnabled) return value;
        value.renderDistanceStart = value.renderDistanceEnd = value.environmentalStart = value.environmentalEnd = value.skyEnd = value.cloudEnd =
                MinecraftClient.getInstance().options.getClampedViewDistance() * 32;
        return value;
    }
}
