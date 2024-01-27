package io.redspace.pvp_flagging.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class PvpFlagCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("pvpFlag")
                .requires((p) -> p.hasPermission(0))
                .executes((context) -> toggleFlag(context.getSource()))
                .then(Commands.literal("on")
                        .executes((context) -> flag(context.getSource())))
                .then(Commands.literal("off")
                        .executes((context) -> unflag(context.getSource())))
                .then(Commands.literal("status")
                        .executes((context) -> status(context.getSource())))
        );
    }

    private static int toggleFlag(CommandSourceStack source) {
        if (PlayerFlagManager.INSTANCE.isPlayerFlagged(source.getPlayer())) {
            unflag(source);
        } else {
            flag(source);
        }
        return 1;
    }

    private static int unflag(CommandSourceStack source) {
        PlayerFlagManager.INSTANCE.unflagPlayer(source.getPlayer());
        return 1;
    }

    private static int flag(CommandSourceStack source) {
        PlayerFlagManager.INSTANCE.flagPlayer(source.getPlayer());
        return 1;
    }

    private static int status(CommandSourceStack source) {
        var isPlayerFlagged = PlayerFlagManager.INSTANCE.isPlayerFlagged(source.getPlayer());
        source.sendSuccess(() -> Component.translatable("ui.pvp_flagging.pvp_status", isPlayerFlagged), true);
        return 1;
    }
}
