package com.example.mixin;

import com.example.ExampleMod;
import com.example.IAddNoChecks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Final
    @Shadow
    PlayerInventory inventory;

    @Shadow
    public abstract Text getDisplayName();


    @Inject(method = "dropInventory", at = @At(value = "HEAD"), cancellable = true)
    private void injected(ServerWorld world, CallbackInfo ci) {
        var is = ExampleMod.CustomBundle.copy();
        var builder = new BundleContentsComponent.Builder(BundleContentsComponent.DEFAULT);
        for (var i : inventory) {
            if (i.isEmpty()) continue;
            if (builder instanceof IAddNoChecks) {
                ((IAddNoChecks) builder).addNoChecks(i);
            }
        }
        is.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
        is.set(DataComponentTypes.CUSTOM_NAME, getDisplayName().copy().append(Text.literal("'s belongings")).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
        var pos = ((PlayerEntity) (Object) this).getBlockPos();
        while (world.getBlockState(pos).isAir() && world.isInBuildLimit(pos)) {
            pos = pos.add(0, 1, 0);
        }
        while (world.getBlockState(pos.down()).isAir() && world.isInBuildLimit(pos)) {
            pos = pos.add(0, -1, 0);
        }
        var itemFrame = new ItemFrameEntity(world, pos, Direction.UP);
        itemFrame.setHeldItemStack(is);
        world.spawnEntity(itemFrame);
        ci.cancel();
    }
}