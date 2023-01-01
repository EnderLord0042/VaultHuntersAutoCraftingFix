package com.enderlord.vaulthuntersautocraftingfix.mixins;

import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import iskallia.vault.research.Restrictions;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GridNetworkNode.class)
public abstract class GridNetworkNodeMixin {
    @Shadow(remap = false)
    public abstract boolean isProcessingPattern();

    @Final
    @Shadow(remap = false)
    private ResultContainer result;

    @Inject(method = "canCreatePattern", at = @At("HEAD"), cancellable = true, remap = false)
    private void preventCreatePattern(CallbackInfoReturnable<Boolean> cir) {
        if (!isProcessingPattern()) {
            String restrictedBy = VaultHuntersAutoCraftingFix.autoCrafterResearchTree.restrictedBy(result.getItem(0).getItem(), Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
