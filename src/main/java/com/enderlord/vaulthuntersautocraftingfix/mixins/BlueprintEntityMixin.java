package com.enderlord.vaulthuntersautocraftingfix.mixins;

import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import com.simibubi.create.content.curiosities.tools.BlueprintEntity;
import iskallia.vault.research.Restrictions;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.checkerframework.common.reflection.qual.Invoke;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(BlueprintEntity.class)
public class BlueprintEntityMixin {
    @ModifyVariable(method = "interactAt", at = @At(value = "STORE", ordinal = 1), remap = true)
    private Optional<CraftingRecipe> preventInteractAt(Optional<CraftingRecipe> recipe) {
        if (recipe.isPresent()) {
            String restrictedBy = VaultHuntersAutoCraftingFix.autoCrafterResearchTree.restrictedBy(((CraftingRecipe) recipe.get()).getResultItem().getItem(), Restrictions.Type.CRAFTABILITY);
            if (restrictedBy != null) {
                return Optional.empty();
            }
        }
        return recipe;
    }
}
