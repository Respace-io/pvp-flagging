package io.redspace.pvp_flagging.registries;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.command.PvpFlagCommand;
import io.redspace.pvp_flagging.command.PvpZoneCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = PvpFlagging.MODID)
public class CommandRegistry {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        var commandDispatcher = event.getDispatcher();
        //var commandBuildContext = event.getBuildContext();
        PvpFlagCommand.register(commandDispatcher);
        PvpZoneCommand.register(commandDispatcher);
    }
}