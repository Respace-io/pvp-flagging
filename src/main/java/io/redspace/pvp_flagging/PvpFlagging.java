package io.redspace.pvp_flagging;

import com.mojang.logging.LogUtils;
import io.redspace.pvp_flagging.client.FlagIndicatorOverlay;
import io.redspace.pvp_flagging.config.PvpConfig;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import io.redspace.pvp_flagging.registries.Network;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PvpConfig.CLIENT.SPEC, String.format("%s-client.toml", PvpFlagging.MODID));
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void init(FMLCommonSetupEvent event) {
        Network.register();
        PlayerFlagManager.init();
        PvpZoneManager.init();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            event.registerBelowAll("pvp_flagging_flag_status_overlay", FlagIndicatorOverlay.INSTANCE);
        }
    }
}
