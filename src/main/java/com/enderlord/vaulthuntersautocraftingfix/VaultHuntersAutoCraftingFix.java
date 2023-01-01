package com.enderlord.vaulthuntersautocraftingfix;

import com.enderlord.vaulthuntersautocraftingfix.mixins.PlayerResearchesDataAccessor;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.PlayerReference;
import iskallia.vault.world.data.PlayerResearchesData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod("vaulthuntersautocraftingfix")
public class VaultHuntersAutoCraftingFix {
    public static ResearchTree autoCrafterResearchTree = ResearchTree.empty();
    public static List<PlayerReference> autoCrafterResearchTeam = new ArrayList<>();

    public VaultHuntersAutoCraftingFix() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResearchTree getTeamResearchesDone(List<PlayerReference> team, PlayerResearchesData researchData) {
        List<String> teamResearches = new ArrayList<>();
        for (PlayerReference player : team) {
            List<String> playerResearches = researchData.getResearches(player.getId()).getResearchesDone();
            playerResearches.removeAll(teamResearches);
            teamResearches.addAll(playerResearches);
        }
        CompoundTag researchNBT = new CompoundTag();

        ListTag researches = new ListTag();
        ListTag shares = new ListTag();

        teamResearches.forEach((researchName) -> {
            CompoundTag research = new CompoundTag();
            research.putString("name", researchName);
            researches.add(research);
        });

        researchNBT.put("researches", researches);
        researchNBT.put("shares", shares);

        ResearchTree teamTree = ResearchTree.empty();
        teamTree.deserializeNBT(researchNBT);

        return teamTree;
    }

    public static void reassessAutoCrafterResearchTeam(PlayerResearchesData researchData) {
        List<List<PlayerReference>> researchTeams = ( (PlayerResearchesDataAccessor) researchData).getResearchTeams();

        List<List<PlayerReference>> possibleTeams = new ArrayList<>();
        int largestTeam = 0;

        for (List<PlayerReference> researchTeam : researchTeams) {
            int currentTeamSize = researchTeam.size();
            if (currentTeamSize > largestTeam) {
                largestTeam = currentTeamSize;
                possibleTeams = new ArrayList<>();
                possibleTeams.add(researchTeam);
            } else if (currentTeamSize == largestTeam) {
                possibleTeams.add(researchTeam);
            }
        }

        if (possibleTeams.size() > 1) {

            List<PlayerReference> smartestTeam = new ArrayList<>();
            int largestResearchAmount = 0;

            for (List<PlayerReference> team: possibleTeams) {
                int currentTeamResearchAmount = getTeamResearchesDone(team, researchData).getResearchesDone().size();
                if (currentTeamResearchAmount > largestResearchAmount) {
                    largestResearchAmount = currentTeamResearchAmount;
                    smartestTeam = new ArrayList<>(team);
                } else if (currentTeamResearchAmount == largestResearchAmount) {
                    smartestTeam.addAll(team);
                }
            }
            autoCrafterResearchTeam = smartestTeam;
            autoCrafterResearchTree = getTeamResearchesDone(autoCrafterResearchTeam, researchData);
        } else if (possibleTeams.size() == 1) {
            autoCrafterResearchTeam = possibleTeams.get(0);
            autoCrafterResearchTree = getTeamResearchesDone(autoCrafterResearchTeam, researchData);
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isClientSide()) {
            reassessAutoCrafterResearchTeam(PlayerResearchesData.get(Objects.requireNonNull(event.getWorld().getServer()).overworld()));
        }
    }
}
