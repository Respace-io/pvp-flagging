package io.redspace.pvp_flagging.data;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
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
        ListTag allZoneManagers = new ListTag();
        for (Map.Entry<ResourceKey<Level>, PvpZoneManager> entry : PvpZoneManager.INSTANCES.entrySet()) {
            CompoundTag tuple = new CompoundTag();
            tuple.putString("level", entry.getKey().location().toString());
            tuple.put("manager", entry.getValue().serializeNBT());
            allZoneManagers.add(tuple);
        }
        compoundTag.put("PvpZoneManager", allZoneManagers);
        compoundTag.put("PlayerFlagManager", PlayerFlagManager.INSTANCE.serializeNBT());
        return compoundTag;
    }

    public static PvpDataStorage load(CompoundTag tag) {

        if (tag.contains("PvpZoneManager")) {
            ListTag allZoneManagers = tag.getList("PvpZoneManager", ListTag.TAG_COMPOUND);
            for (Tag t : allZoneManagers) {
                CompoundTag tuple = (CompoundTag) t;
                ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tuple.getString("level")));
                PvpZoneManager.getInstance(dimension).deserializeNBT(tuple.getCompound("manager"));
            }
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
