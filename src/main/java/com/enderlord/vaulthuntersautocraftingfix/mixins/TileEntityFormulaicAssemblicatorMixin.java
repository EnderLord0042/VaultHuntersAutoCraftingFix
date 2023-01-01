package com.enderlord.vaulthuntersautocraftingfix.mixins;

import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import iskallia.vault.research.Restrictions;
import mekanism.common.tile.machine.TileEntityFormulaicAssemblicator;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityFormulaicAssemblicator.class)
public class TileEntityFormulaicAssemblicatorMixin {
    @Shadow(remap = false)
    private ItemStack lastOutputStack;
    @Shadow(remap = false)
    private boolean isRecipe;

    @Inject(method = "recalculateRecipe", at = @At("TAIL"), remap = false)
    private void preventRecalculateRecipe(CallbackInfo ci) {
        if (!lastOutputStack.isEmpty()) {
            String restrictedBy = VaultHuntersAutoCraftingFix.autoCrafterResearchTree.restrictedBy(lastOutputStack.getItem(), Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
                lastOutputStack = ItemStack.EMPTY;
                isRecipe = false;
            }
        }
    }
}
