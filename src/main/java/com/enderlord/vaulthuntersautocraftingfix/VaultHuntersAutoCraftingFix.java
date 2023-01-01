package com.enderlord.vaulthuntersautocraftingfix;

import com.mojang.logging.LogUtils;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.PlayerReference;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod("vaulthuntersautocraftingfix")
public class VaultHuntersAutoCraftingFix {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResearchTree autoCrafterResearchTree = ResearchTree.empty();

    public static List<UUID> autoCrafterResearchTeam = new ArrayList<>();

    public VaultHuntersAutoCraftingFix() {}
}
