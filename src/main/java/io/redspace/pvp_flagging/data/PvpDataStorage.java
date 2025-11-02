package io.redspace.pvp_flagging.data;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.util.Map;

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
        ListTag allZoneManagers = new ListTag();
        for (Map.Entry<ResourceKey<Level>, PvpZoneManager> entry : PvpZoneManager.INSTANCES.entrySet()) {
            CompoundTag tuple = new CompoundTag();
            tuple.putString("level", entry.getKey().location().toString());
            tuple.put("manager", entry.getValue().serializeNBT(provider));
            allZoneManagers.add(tuple);
        }
        compoundTag.put("PvpZoneManager", allZoneManagers);
        compoundTag.put("PlayerFlagManager", PlayerFlagManager.INSTANCE.serializeNBT(provider));
        return compoundTag;
    }

    public static PvpDataStorage load(CompoundTag tag, HolderLookup.Provider provider) {

        if (tag.contains("PvpZoneManager")) {
            ListTag allZoneManagers = tag.getList("PvpZoneManager", ListTag.TAG_COMPOUND);
            for (Tag t : allZoneManagers) {
                CompoundTag tuple = (CompoundTag) t;
                ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tuple.getString("level")));
                PvpZoneManager.getInstance(dimension).deserializeNBT(provider, tuple.getCompound("manager"));
            }
        }

        if (tag.contains("PlayerFlagManager")) {
            PlayerFlagManager.INSTANCE.deserializeNBT(provider, (CompoundTag) tag.get("PlayerFlagManager"));
        }

        return new PvpDataStorage();
    }

    @SubscribeEvent
    public static void onServerStartedEvent(ServerStartedEvent event) {
        PvpDataStorage.init(event.getServer().overworld().getDataStorage());
    }
}
