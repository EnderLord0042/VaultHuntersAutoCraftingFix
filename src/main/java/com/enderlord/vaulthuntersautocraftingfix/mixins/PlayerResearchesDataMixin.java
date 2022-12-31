package com.enderlord.vaulthuntersautocraftingfix.mixins;

import com.enderlord.vaulthuntersautocraftingfix.VaultHuntersAutoCraftingFix;
import iskallia.vault.research.type.Research;
import iskallia.vault.world.data.PlayerResearchesData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(PlayerResearchesData.class)
public abstract class PlayerResearchesDataMixin {
    @Inject(method = "research", at = @At("HEAD"), remap = false)
    public void autoCrafterResearch(ServerPlayer player, Research research, CallbackInfoReturnable<PlayerResearchesData> cir) {
        if (VaultHuntersAutoCraftingFix.autoCrafterResearchTeam.stream().anyMatch(playerReference -> playerReference.getId().equals(player.getUUID()))) {
            VaultHuntersAutoCraftingFix.autoCrafterResearchTree.research(research);
        }
    }

    @Inject(method = "removeResearch", at = @At("HEAD"), remap = false)
    public void autoCrafterRemoveResearch(ServerPlayer player, Research research, CallbackInfoReturnable<PlayerResearchesData> cir) {
        if (VaultHuntersAutoCraftingFix.autoCrafterResearchTeam.stream().anyMatch(playerReference -> playerReference.getId().equals(player.getUUID()))) {
            VaultHuntersAutoCraftingFix.autoCrafterResearchTree.removeResearch(research);
        }
    }

    @Inject(method = "leaveCurrentTeam", at = @At("HEAD"), remap = false)
    public void autoCrafterLeaveCurrentTeam(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (VaultHuntersAutoCraftingFix.autoCrafterResearchTeam.stream().anyMatch(playerReference -> playerReference.getId().equals(player.getUUID()))) {
            VaultHuntersAutoCraftingFix.reassessAutoCrafterResearchTeam(PlayerResearchesData.get(Objects.requireNonNull(player.getServer()).overworld()));
        }
    }

    @Inject(method = "acceptInvite", at = @At("HEAD"), remap = false)
    public void autoCrafterAcceptInvite(Player invitee, UUID issuer, CallbackInfoReturnable<Boolean> cir) {
        VaultHuntersAutoCraftingFix.reassessAutoCrafterResearchTeam(PlayerResearchesData.get(Objects.requireNonNull(invitee.getServer()).overworld()));
    }
}
