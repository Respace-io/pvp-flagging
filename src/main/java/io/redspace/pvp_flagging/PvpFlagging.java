package io.redspace.pvp_flagging;

import com.mojang.logging.LogUtils;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import io.redspace.pvp_flagging.registries.Network;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(PvpFlagging.MODID)
public class PvpFlagging {
    public static final String MODID = "pvp_flagging";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PvpFlagging() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(PvpFlagging::init);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PvpConfig.SERVER.SPEC, String.format("%s-server.toml", PvpFlagging.MODID));
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void init(FMLCommonSetupEvent event) {
        Network.register();
        PlayerFlagManager.init();
        PvpZoneManager.init();
    }
}
