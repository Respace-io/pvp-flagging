package io.redspace.pvp_flagging.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.pvp_flagging.core.PvpZone;
import io.redspace.pvp_flagging.core.PvpZoneManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class PvpZoneCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("pvpZone")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("add")
                        .then((Commands.argument("name", StringArgumentType.string()))
                                .then((Commands.argument("x1", IntegerArgumentType.integer()))
                                        .then((Commands.argument("z1", IntegerArgumentType.integer()))
                                                .then((Commands.argument("x2", IntegerArgumentType.integer()))
                                                        .then((Commands.argument("z2", IntegerArgumentType.integer()))
                                                                .then((Commands.argument("buffer", IntegerArgumentType.integer())
                                                                        .executes((context) -> addZone(
                                                                                context.getSource(),
                                                                                StringArgumentType.getString(context, "name"),
                                                                                IntegerArgumentType.getInteger(context, "x1"),
                                                                                IntegerArgumentType.getInteger(context, "z1"),
                                                                                IntegerArgumentType.getInteger(context, "x2"),
                                                                                IntegerArgumentType.getInteger(context, "z2"),
                                                                                IntegerArgumentType.getInteger(context, "buffer"))
                                                                        ))))))))
                ).then((Commands.literal("add_chunk")
                        .then((Commands.argument("buffer", IntegerArgumentType.integer())
                                .executes((context) -> addZoneChunk(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "buffer"))
                                )))
                        .executes((context) -> addZoneChunk(
                                context.getSource(), 8
                        ))))
                .then(Commands.literal("remove")
                        .then((Commands.argument("name", StringArgumentType.string()))
                                .executes((context) -> removeZone(context.getSource(), StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("list")
                        .executes((context) -> listZones(context.getSource())))
        );
    }

    private static int listZones(CommandSourceStack source) {
        var server = source.getServer();
        var sb = new StringBuilder();
        var gold = "§6";
        var white = "§f";
        var red = "§c";
        for (Level level : server.getAllLevels()) {
            PvpZoneManager instance = PvpZoneManager.getInstance(level);
            if (instance.getZones().isEmpty()) {
                continue;
            }
            sb.append(gold).append(String.format("[%s]:\n", level.dimension().identifier()));
            instance.getZones().forEach(zone -> sb.append("   ").append(red).append("* ").append(white).append(zone).append("\n"));
        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1); // pop last newline
            source.sendSuccess(() -> Component.literal(sb.toString()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("command.pvp_flagging.zone.list.empty"), true);
        }
        return 1;
    }

    private static int addZoneChunk(CommandSourceStack source, int buffer) {
        var position = source.getPosition();
        int chunkX = (int) (position.x / 16);
        int chunkZ = (int) (position.z / 16);
        int minX = chunkX * 16;
        int minZ = chunkZ * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        return addZone(source, String.format("Chunk(%s,%s)", chunkX, chunkZ), minX, minZ, maxX, maxZ, buffer);
    }

    private static int addZone(CommandSourceStack source, String name, int x1, int z1, int x2, int z2, int buffer) {
        var level = source.getLevel();
        var instance = PvpZoneManager.getInstance(level);
        var pvpZone = new PvpZone(name, x1, z1, x2, z2, buffer);
        if (instance.addZone(pvpZone)) {
            source.sendSuccess(() -> Component.translatable("command.pvp_flagging.zone.add.success", pvpZone.getName()), false);
            return 1;
        } else {
            source.sendFailure(Component.translatable("command.pvp_flagging.zone.add.fail", pvpZone.getName()));
            return 0;
        }
    }

    private static int removeZone(CommandSourceStack source, String name) {
        var level = source.getLevel();
        var instance = PvpZoneManager.getInstance(level);
        instance.removeZone(name);
        return 1;
    }
}
