package com.larpcraft.larp;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class LarpCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("larp")
                            .executes(context -> {
                                List<String> names = new ArrayList<>();
                                for (LarpCharacter character : CharacterRegistry.all()) {
                                    names.add(character.displayName());
                                }
                                String message = "Available characters: " + String.join(", ", names);

                                context.getSource().sendSuccess(
                                        () -> Component.literal(message),
                                        false
                                );
                                return 1;
                            })
            );
        });
    }
}