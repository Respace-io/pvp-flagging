package io.redspace.pvp_flagging;

import com.mojang.logging.LogUtils;
import io.redspace.pvp_flagging.client.FlagIndicatorOverlay;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.slf4j.Logger;

@Mod(PvpFlagging.MODID)
public class PvpFlagging {
    public static final String MODID = "pvp_flagging";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PvpFlagging(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(PvpFlagging::init);
        modContainer.registerConfig(ModConfig.Type.SERVER, PvpConfig.SERVER.SPEC, String.format("%s-server.toml", PvpFlagging.MODID));
        modContainer.registerConfig(ModConfig.Type.CLIENT, PvpConfig.CLIENT.SPEC, String.format("%s-client.toml", PvpFlagging.MODID));
    }

    public static void init(FMLCommonSetupEvent event) {
        PlayerFlagManager.init();
        PvpZoneManager.init();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(PvpFlagging.MODID, path);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiLayersEvent event) {
            event.registerBelowAll(PvpFlagging.id("flag_status_overlay"), FlagIndicatorOverlay.INSTANCE);
        }
    }
}
