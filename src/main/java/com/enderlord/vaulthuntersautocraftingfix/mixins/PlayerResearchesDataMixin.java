package com.enderlord.vaulthuntersautocraftingfix.mixins;

import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.PlayerReference;
import iskallia.vault.world.data.PlayerResearchesData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(PlayerResearchesData.class)
public abstract class PlayerResearchesDataMixin {
    @Final
    @Shadow(remap = false)
    private List<List<PlayerReference>> researchTeams;

    @Final
    @Shadow(remap = false)
    private Map<UUID, ResearchTree> playerMap;

    @Shadow(remap = false)
    public abstract ResearchTree getResearches(UUID uuid);

    @Inject(method = "research", at = @At("HEAD"), remap = false)
    private void autoCrafterResearch(ServerPlayer player, Research research, CallbackInfoReturnable<PlayerResearchesData> cir) {
        if (VaultHuntersAutoCraftingFix.autoCrafterResearchTeam.stream().anyMatch(playerReference -> playerReference.equals(player.getUUID()))) {
            VaultHuntersAutoCraftingFix.autoCrafterResearchTree.research(research);
        }
    }

    @Inject(method = "removeResearch", at = @At("HEAD"), remap = false)
    private void autoCrafterRemoveResearch(ServerPlayer player, Research research, CallbackInfoReturnable<PlayerResearchesData> cir) {
        if (VaultHuntersAutoCraftingFix.autoCrafterResearchTeam.stream().anyMatch(playerReference -> playerReference.equals(player.getUUID()))) {
            VaultHuntersAutoCraftingFix.autoCrafterResearchTree.removeResearch(research);
        }
    }

    @Inject(method = "leaveCurrentTeam", at = @At("HEAD"), remap = false)
    private void autoCrafterLeaveCurrentTeam(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (VaultHuntersAutoCraftingFix.autoCrafterResearchTeam.stream().anyMatch(playerReference -> playerReference.equals(player.getUUID()))) {
            reassessAutoCrafterResearchTeam();
        }
    }

    @Inject(method = "acceptInvite", at = @At("HEAD"), remap = false)
    private void autoCrafterAcceptInvite(Player invitee, UUID issuer, CallbackInfoReturnable<Boolean> cir) {
        reassessAutoCrafterResearchTeam();
    }

    @Inject(method = "load", at = @At("TAIL"), remap = false)
    private void autoCrafterload(CallbackInfo ci){
        reassessAutoCrafterResearchTeam();
    }

    private void reassessAutoCrafterResearchTeam() {
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
                int currentTeamResearchAmount = getTeamResearchesDone(team.stream().map(PlayerReference::getId).collect(Collectors.toList())).getResearchesDone().size();
                if (currentTeamResearchAmount > largestResearchAmount) {
                    largestResearchAmount = currentTeamResearchAmount;
                    smartestTeam = new ArrayList<>(team);
                } else if (currentTeamResearchAmount == largestResearchAmount) {
                    smartestTeam.addAll(team);
                }
            }
            VaultHuntersAutoCraftingFix.autoCrafterResearchTeam = smartestTeam.stream().map(PlayerReference::getId).collect(Collectors.toList());
            VaultHuntersAutoCraftingFix.autoCrafterResearchTree = getTeamResearchesDone(VaultHuntersAutoCraftingFix.autoCrafterResearchTeam);
        } else if (possibleTeams.size() == 1) {
            VaultHuntersAutoCraftingFix.autoCrafterResearchTeam = possibleTeams.get(0).stream().map(PlayerReference::getId).collect(Collectors.toList());
            VaultHuntersAutoCraftingFix.autoCrafterResearchTree = getTeamResearchesDone(VaultHuntersAutoCraftingFix.autoCrafterResearchTeam);
        } else  {
            VaultHuntersAutoCraftingFix.autoCrafterResearchTeam = playerMap.keySet().stream().toList();
            VaultHuntersAutoCraftingFix.autoCrafterResearchTree = combineResearchTrees(playerMap.values().stream().toList());
        }
    }

    private ResearchTree getTeamResearchesDone(List<UUID> team) {
        List<String> teamResearches = new ArrayList<>();
        for (UUID playerID : team) {
            List<String> playerResearches = getResearches(playerID).getResearchesDone();
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

    private ResearchTree combineResearchTrees(List<ResearchTree> trees) {
        List<String> combinedResearches = new ArrayList<>();
        for (ResearchTree tree : trees) {
            List<String> treeResearches = tree.getResearchesDone();
            treeResearches.removeAll(combinedResearches);
            combinedResearches.addAll(treeResearches);
        }
        CompoundTag researchNBT = new CompoundTag();

        ListTag researches = new ListTag();
        ListTag shares = new ListTag();

        combinedResearches.forEach((researchName) -> {
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

}
