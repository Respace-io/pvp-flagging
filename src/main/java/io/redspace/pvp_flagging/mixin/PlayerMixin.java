package io.redspace.pvp_flagging.mixin;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "canHarmPlayer", at = @At(value = "HEAD"), cancellable = true)
    public void canHarmPlayer(Player otherPlayer, CallbackInfoReturnable<Boolean> cir) {
        var player = (Player) (Object) (this);
        Team team = player.getTeam();
        Team team1 = otherPlayer.getTeam();
        if (team == null) {
            if (PlayerFlagManager.INSTANCE.areBothPlayersFlagged(player, otherPlayer)) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        } else {
            if (!team.isAlliedTo(team1)) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(team.isAllowFriendlyFire());
            }
        }
    }
}