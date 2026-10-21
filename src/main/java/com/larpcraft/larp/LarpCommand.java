package com.larpcraft.larp;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class LarpCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LARPCRAFT.LOGGER.info("Registering /larp command"); // NEW
            dispatcher.register(
                    Commands.literal("larp")
                            .executes(context -> {
                                context.getSource().sendSuccess(
                                        () -> Component.literal("Larp mod is working!"),
                                        false
                                );
                                return 1;
                            })
            );
        });
    }
}