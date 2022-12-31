package com.enderlord.vaulthuntersautocraftingfix.mixins;

import iskallia.vault.util.PlayerReference;
import iskallia.vault.world.data.PlayerResearchesData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerResearchesData.class)
public interface PlayerResearchesDataAccessor {
    @Accessor(remap = false)
    List<List<PlayerReference>> getResearchTeams();
}
