package io.redspace.pvp_flagging.mixin;

import io.redspace.pvp_flagging.client.ClientHelper;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "decorateName", at = @At(value = "RETURN"), cancellable = true)
    private void decorateFlagIndicator(PlayerInfo pPlayerInfo, MutableComponent pName, CallbackInfoReturnable<Component> cir) {
        if (ClientHelper.isFlagged(pPlayerInfo.getProfile().getId())) {
            // return value is always a mutable component
            cir.setReturnValue(((MutableComponent) cir.getReturnValue()).append(ClientHelper.nameTagIndicator));
        }
    }
}
