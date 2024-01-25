package io.redspace.pvp_flagging.registries;

import io.redspace.pvp_flagging.command.PvpFlagCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class CommandRegistry {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        var commandDispatcher = event.getDispatcher();
        var commandBuildContext = event.getBuildContext();
        PvpFlagCommand.register(commandDispatcher);
    }
}