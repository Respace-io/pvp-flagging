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

public class PvpZoneCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("pvpZone")
                .requires((p) -> p.hasPermission(2))
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
                                                                        )))))))))
                .then(Commands.literal("remove")
                        .then((Commands.argument("name", StringArgumentType.string()))
                                .executes((context) -> removeZone(context.getSource(), StringArgumentType.getString(context, "name")))))
                .then(Commands.literal("list")
                        .executes((context) -> listZones(context.getSource())))
        );
    }

    private static int listZones(CommandSourceStack source) {
        if (PvpZoneManager.INSTANCE != null) {
            var sb = new StringBuilder();
            PvpZoneManager.INSTANCE.getZones().forEach(zone -> sb.append(zone).append("\n"));
            source.sendSuccess(() -> Component.literal(sb.toString()), true);
        }
        return 1;
    }

    private static int addZone(CommandSourceStack source, String name, int x1, int z1, int x2, int z2, int buffer) {
        if (PvpZoneManager.INSTANCE != null) {
            var pvpZone = new PvpZone(name, x1, z1, x2, z2, buffer);
            return PvpZoneManager.INSTANCE.addZone(pvpZone) ? 1 : 0;
        }
        return 0;
    }

    private static int removeZone(CommandSourceStack source, String name) {
        if (PvpZoneManager.INSTANCE != null) {
            PvpZoneManager.INSTANCE.removeZone(name);
        }
        return 1;
    }
}
