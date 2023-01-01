package com.enderlord.vaulthuntersautocraftingfix.mixins;

import cofh.thermal.expansion.block.entity.machine.MachineCrafterTile;
import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import iskallia.vault.research.Restrictions;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(MachineCrafterTile.class)
public class MachineCrafterTileMixin {

    @ModifyVariable(method = "setRecipe", at = @At(value = "STORE"), remap = false)
    private Optional<CraftingRecipe> preventValidRecipe(Optional<CraftingRecipe> possibleRecipe) {
        if (possibleRecipe.isPresent()) {
            String restrictedBy = VaultHuntersAutoCraftingFix.autoCrafterResearchTree.restrictedBy(((CraftingRecipe)possibleRecipe.get()).getResultItem().getItem(), Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
                return Optional.empty();
            }
        }
        return possibleRecipe;
    }
}
