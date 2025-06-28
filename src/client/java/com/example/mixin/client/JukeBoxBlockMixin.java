package com.example.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SoundSystem.class)
public abstract class JukeBoxBlockMixin {

    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"), ordinal = 0)
    private SoundInstance.AttenuationType updateAttenuation(SoundInstance.AttenuationType value, @Local(argsOnly = true) SoundInstance sound) {
        return sound.getCategory() == SoundCategory.RECORDS ? SoundInstance.AttenuationType.NONE : value;
    }

    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"), ordinal = 0)
    private boolean updateRelative(boolean value, @Local(argsOnly = true) SoundInstance sound) {
        return sound.getCategory() == SoundCategory.RECORDS || value;
    }

    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundListener;method_72231()F"), ordinal = 0)
    private Vec3d updatePosition(Vec3d value, @Local(argsOnly = true) SoundInstance sound) {
        return sound.getCategory() == SoundCategory.RECORDS ? new Vec3d(0, 0, 0) : value;
    }


}
