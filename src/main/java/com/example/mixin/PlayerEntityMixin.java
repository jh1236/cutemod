package com.example.mixin;

import com.example.ExampleMod;
import com.example.IAddNoChecks;
import net.minecraft.block.Blocks;
import net.minecraft.component.*;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.xml.crypto.Data;

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
        var startPos = ((PlayerEntity) (Object) this).getBlockPos();
        var pos = startPos;
        if (!world.isInBuildLimit(pos) && pos.getY() > 0) {
            //we died somewhere where you can't place a chest; so the player can get their own items
            return;
        }
        while (!world.getBlockState(pos).isReplaceable()) {
            pos = pos.add(0, 1, 0);
            if (!world.isInBuildLimit(pos)) {
                pos = new BlockPos(pos.getX() + 1, startPos.getY(), pos.getZ());
            }
        }

        world.setBlockState(pos, Blocks.CHEST.getDefaultState());
        var grave = (ChestBlockEntity) world.getBlockEntity(pos);
        grave.readComponents(ComponentMap.EMPTY,
                ComponentChanges.builder().add(DataComponentTypes.CUSTOM_NAME, Text.literal("Here Lies ").append(getDisplayName())).build());
        grave.setStack(13, is);

        var player = (PlayerEntity) (Object) this;
        var playerHead = Items.PLAYER_HEAD.getDefaultStack();
        playerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(player.getGameProfile()));
        grave.setStack(4, playerHead);

        ci.cancel();
    }
}