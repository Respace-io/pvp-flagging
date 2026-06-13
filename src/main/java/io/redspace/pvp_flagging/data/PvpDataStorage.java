package io.redspace.pvp_flagging.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.Map;

@EventBusSubscriber(modid = PvpFlagging.MODID)
public class PvpDataStorage extends SavedData {
    public static PvpDataStorage INSTANCE;

    private static final Codec<PvpDataStorage> CODEC = CompoundTag.CODEC.flatXmap(
            tag -> {
                loadFromTag(tag);
                return DataResult.success(new PvpDataStorage());
            },
            data -> DataResult.success(buildTag())
    );

    public static final SavedDataType<PvpDataStorage> TYPE = new SavedDataType<>(
            PvpFlagging.id("data"),
            PvpDataStorage::new,
            CODEC,
            null
    );

    public static void init(MinecraftServer server) {
        if (server != null) {
            PvpDataStorage.INSTANCE = server.getDataStorage().computeIfAbsent(TYPE);
        }
    }

    public static void markDirty() {
        if (INSTANCE != null) {
            INSTANCE.setDirty();
        }
    }

    private static CompoundTag buildTag() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag allZoneManagers = new ListTag();
        for (Map.Entry<ResourceKey<Level>, PvpZoneManager> entry : PvpZoneManager.INSTANCES.entrySet()) {
            CompoundTag tuple = new CompoundTag();
            tuple.putString("level", entry.getKey().identifier().toString());
            tuple.put("manager", entry.getValue().save());
            allZoneManagers.add(tuple);
        }
        compoundTag.put("PvpZoneManager", allZoneManagers);
        compoundTag.put("PlayerFlagManager", PlayerFlagManager.INSTANCE.save());
        return compoundTag;
    }

    private static void loadFromTag(CompoundTag tag) {
        if (tag.contains("PvpZoneManager")) {
            ListTag allZoneManagers = tag.getList("PvpZoneManager").orElse(new ListTag());
            for (Tag t : allZoneManagers) {
                CompoundTag tuple = (CompoundTag) t;
                ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, Identifier.parse(tuple.getString("level").orElse("minecraft:overworld")));
                PvpZoneManager.getInstance(dimension).load(tuple.getCompound("manager").orElse(new CompoundTag()));
            }
        }

        if (tag.contains("PlayerFlagManager")) {
            tag.getCompound("PlayerFlagManager").ifPresent(compoundTag -> PlayerFlagManager.INSTANCE.load(compoundTag));
        }
    }

    @SubscribeEvent
    public static void onServerStartedEvent(ServerStartedEvent event) {
        PvpDataStorage.init(event.getServer());
    }
}
