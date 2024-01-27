package io.redspace.pvp_flagging.data;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class PvpDataStorage extends SavedData {
    public static PvpDataStorage INSTANCE;

    public static void init(DimensionDataStorage dimensionDataStorage) {
        if (dimensionDataStorage != null) {
            PvpDataStorage.INSTANCE = dimensionDataStorage.computeIfAbsent(
                    PvpDataStorage::load,
                    PvpDataStorage::new,
                    PvpFlagging.MODID);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag pCompoundTag) {
        pCompoundTag.put("PvpZoneManager", PvpZoneManager.INSTANCE.serializeNBT());
        pCompoundTag.put("PlayerFlagManager", PlayerFlagManager.INSTANCE.serializeNBT());
        return pCompoundTag;
    }

    public static PvpDataStorage load(CompoundTag tag) {

        if (tag.contains("PvpZoneManager")) {
            PvpZoneManager.INSTANCE.deserializeNBT((CompoundTag) tag.get("PvpZoneManager"));
        }

        if (tag.contains("PlayerFlagManager")) {
            PlayerFlagManager.INSTANCE.deserializeNBT((CompoundTag) tag.get("PlayerFlagManager"));
        }

        return new PvpDataStorage();
    }

    @SubscribeEvent
    public static void onServerStartedEvent(ServerStartedEvent event) {
        PvpDataStorage.init(event.getServer().overworld().getDataStorage());
    }
}
