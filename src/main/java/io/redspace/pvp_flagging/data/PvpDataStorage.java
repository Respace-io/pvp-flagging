package io.redspace.pvp_flagging.data;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber
public class PvpDataStorage extends SavedData {
    public static PvpDataStorage INSTANCE;

    public static void init(DimensionDataStorage dimensionDataStorage) {
        if (dimensionDataStorage != null) {
            PvpDataStorage.INSTANCE = dimensionDataStorage.computeIfAbsent(
                    new Factory<PvpDataStorage>(PvpDataStorage::new, PvpDataStorage::load),
                    PvpFlagging.MODID);
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("PvpZoneManager", PvpZoneManager.INSTANCE.serializeNBT(null));
        compoundTag.put("PlayerFlagManager", PlayerFlagManager.INSTANCE.serializeNBT(null));
        return compoundTag;
    }

    public static PvpDataStorage load(CompoundTag tag, HolderLookup.Provider pRegistries) {

        if (tag.contains("PvpZoneManager")) {
            PvpZoneManager.INSTANCE.deserializeNBT(null, (CompoundTag) tag.get("PvpZoneManager"));
        }

        if (tag.contains("PlayerFlagManager")) {
            PlayerFlagManager.INSTANCE.deserializeNBT(null, (CompoundTag) tag.get("PlayerFlagManager"));
        }

        return new PvpDataStorage();
    }

    @SubscribeEvent
    public static void onServerStartedEvent(ServerStartedEvent event) {
        PvpDataStorage.init(event.getServer().overworld().getDataStorage());
    }
}
