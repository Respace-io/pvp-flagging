package io.redspace.pvp_flagging.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.pvp_flagging.core.PlayerFlagManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PvpFlagCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("pvpFlag")
                .requires((p) -> p.hasPermission(0))
                .then(Commands.literal("on")
                        .executes((context) -> flag(context.getSource())))
                .then(Commands.literal("off")
                        .executes((context) -> unflag(context.getSource())))
        );
    }

    private static int unflag(CommandSourceStack source) {
        //TODO: This timout should be configurable
        PlayerFlagManager.INSTANCE.unflagPlayer(source.getPlayer(), source.getLevel().getGameTime() + 20 * 60);
        return 1;
    }

    private static int flag(CommandSourceStack source) {
        PlayerFlagManager.INSTANCE.flagPlayer(source.getPlayer());
        return 1;
    }
}
