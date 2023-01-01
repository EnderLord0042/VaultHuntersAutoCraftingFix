package com.enderlord.vaulthuntersautocraftingfix.mixins;

import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.PatternTermSlot;
import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import iskallia.vault.research.Restrictions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermMenu.class)
public class PatternEncodingTermMenuMixin {
    @Final
    @Shadow(remap = false)
    private PatternTermSlot craftOutputSlot;

    @Inject(method = "encode", at = @At("HEAD"), cancellable = true, remap = false)
    private void preventEncode(CallbackInfo ci) {
        String restrictedBy = VaultHuntersAutoCraftingFix.autoCrafterResearchTree.restrictedBy(craftOutputSlot.getItem().getItem(), Restrictions.Type.CRAFTABILITY);
        if (restrictedBy != null) {
            ci.cancel();
        }
    }
}
