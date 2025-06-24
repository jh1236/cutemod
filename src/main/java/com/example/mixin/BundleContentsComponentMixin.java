package com.example.mixin;

import com.example.IAddNoChecks;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(BundleContentsComponent.Builder.class)
public abstract class BundleContentsComponentMixin implements IAddNoChecks {

    @Final
    @Shadow
    private List<ItemStack> stacks;


    @Unique
    public void addNoChecks(ItemStack stack) {
        this.stacks.addFirst(stack);
    }
}