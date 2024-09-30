package io.redspace.pvp_flagging;

import com.mojang.logging.LogUtils;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(PvpFlagging.MODID)
public class PvpFlagging {
    public static final String MODID = "pvp_flagging";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PvpFlagging(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(PvpFlagging::init);
        modContainer.registerConfig(ModConfig.Type.SERVER, PvpConfig.SERVER.SPEC, String.format("%s-server.toml", PvpFlagging.MODID));
    }

    public static void init(FMLCommonSetupEvent event) {
        PlayerFlagManager.init();
        PvpZoneManager.init();
    }
}
