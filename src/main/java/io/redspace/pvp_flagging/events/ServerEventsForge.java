package io.redspace.pvp_flagging.events;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.config.ServerConfig;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvpFlagging.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventsForge {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (PlayerFlagManager.INSTANCE != null) {
                PlayerFlagManager.INSTANCE.syncToPlayer(serverPlayer);
                switch (PvpConfig.SERVER.PLAYER_LOGIN_STATE.get()) {
                    case FLAG -> PlayerFlagManager.INSTANCE.flagPlayer(serverPlayer);
                    case UNFLAG -> PlayerFlagManager.INSTANCE.unflagPlayerImmediate(serverPlayer);
                }
                PlayerFlagManager.INSTANCE.syncToPlayer(serverPlayer);
            }
            if (PvpConfig.SERVER.WELCOME_MESSAGE.get() && !serverPlayer.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getBoolean("pvp_flagging_hint")) {
                var compound = serverPlayer.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                compound.putBoolean("pvp_flagging_hint", true);
                serverPlayer.getPersistentData().put(Player.PERSISTED_NBT_TAG, compound);
                serverPlayer.sendSystemMessage(
                        Component.translatable("ui.pvp_flagging.system_message",
                                Component.translatable("ui.pvp_flagging.welcome",
                                        Component.translatable("ui.pvp_flagging.welcome.command_hint")
                                                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pvpFlag")))
                                                .withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE)
                                ).withStyle(ChatFormatting.YELLOW)
                        ).withStyle(ChatFormatting.GOLD)
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (PlayerFlagManager.INSTANCE != null) {
                PlayerFlagManager.INSTANCE.cancelScheduledUnflag(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (PlayerFlagManager.INSTANCE != null) {
                switch (PvpConfig.SERVER.PLAYER_RESPAWN_STATE.get()) {
                    case FLAG -> PlayerFlagManager.INSTANCE.flagPlayer(serverPlayer);
                    case UNFLAG -> PlayerFlagManager.INSTANCE.unflagPlayerImmediate(serverPlayer);
                }
                PlayerFlagManager.INSTANCE.syncToPlayer(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        boolean pvp = victim instanceof ServerPlayer && attacker instanceof ServerPlayer;
        if (!pvp) {
            return;
        }
        if (PlayerFlagManager.INSTANCE.anyPlayersScheduledToUnflag()) {
            PlayerFlagManager.INSTANCE.cancelScheduledUnflag((ServerPlayer) victim);
            PlayerFlagManager.INSTANCE.cancelScheduledUnflag((ServerPlayer) attacker);
        }
        float pvpDamageMultiplier = PvpConfig.SERVER.PVP_DAMAGE_MULIPLIER.get().floatValue();
        if (pvpDamageMultiplier != 1.0f) {
            event.setAmount(pvpDamageMultiplier * event.getAmount());
        }
    }
}