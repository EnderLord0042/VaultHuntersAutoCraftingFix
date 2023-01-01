package com.enderlord.vaulthuntersautocraftingfix.mixins;

import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import com.simibubi.create.content.contraptions.components.crafter.RecipeGridHandler;
import iskallia.vault.research.Restrictions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeGridHandler.class)
public class RecipeGridHandlerMixin {
    @Inject(method = "tryToApplyRecipe", at = @At("RETURN"), cancellable = true, remap = false)
    private static void tryToPreventRecipe(Level world, RecipeGridHandler.GroupedItems items, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() != null) {
            String restrictedBy = VaultHuntersAutoCraftingFix.autoCrafterResearchTree.restrictedBy(cir.getReturnValue().getItem(), Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
                cir.setReturnValue(null);
            }
        }
    }
}
