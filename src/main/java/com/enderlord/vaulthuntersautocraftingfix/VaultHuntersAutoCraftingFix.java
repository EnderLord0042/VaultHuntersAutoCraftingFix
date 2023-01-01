package com.enderlord.vaulthuntersautocraftingfix;

import iskallia.vault.research.ResearchTree;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod("vaulthuntersautocraftingfix")
public class VaultHuntersAutoCraftingFix {
    public static ResearchTree autoCrafterResearchTree = ResearchTree.empty();

    public static List<UUID> autoCrafterResearchTeam = new ArrayList<>();

    public VaultHuntersAutoCraftingFix() {}
}
