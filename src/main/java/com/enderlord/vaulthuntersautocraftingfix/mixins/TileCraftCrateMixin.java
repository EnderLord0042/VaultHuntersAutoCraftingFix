package com.enderlord.vaulthuntersautocraftingfix.mixins;

import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import iskallia.vault.research.Restrictions;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.botania.common.block.tile.TileCraftCrate;

import java.util.Optional;

@Mixin(TileCraftCrate.class)
public class TileCraftCrateMixin {
    @ModifyVariable(method = "craft", at = @At(value = "STORE"), remap = false)
    private Optional<CraftingRecipe> preventCraft(Optional<CraftingRecipe> recipe) {
        if (recipe.isPresent()) {
            String restrictedBy = VaultHuntersAutoCraftingFix.autoCrafterResearchTree.restrictedBy(((CraftingRecipe)recipe.get()).getResultItem().getItem(), Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
                return Optional.empty();
            }
        }
        return recipe;
    }
}
