package com.larpcraft.larp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class LarpCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("larp")
                            .executes(LarpCommand::listCharacters)
                            .then(Commands.argument("name", StringArgumentType.word())
                                    .suggests(LarpCommand::suggestCharacters)
                                    .executes(LarpCommand::selectCharacter))
            );
        });
    }

    // Runs for: /larp
    private static int listCharacters(CommandContext<CommandSourceStack> context) {
        List<String> names = new ArrayList<>();
        for (LarpCharacter character : CharacterRegistry.all()) {
            names.add(character.id());
        }
        String message = "Usage: /larp <name>. Available: " + String.join(", ", names);

        context.getSource().sendSuccess(() -> Component.literal(message), false);
        return 1;
    }

    // Runs for: /larp <name>
    private static int selectCharacter(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        Optional<LarpCharacter> found = CharacterRegistry.get(name);

        if (found.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Unknown character: " + name));
            return 0;
        }

        LarpCharacter character = found.get();
        context.getSource().sendSuccess(
                () -> Component.literal("You are now larping as " + character.displayName() + ". " + character.description()),
                false
        );
        return 1;
    }

    // Supplies the tab-complete list for the <name> argument
    private static CompletableFuture<Suggestions> suggestCharacters(
            CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String typed = builder.getRemainingLowerCase();
        for (LarpCharacter character : CharacterRegistry.all()) {
            if (character.id().startsWith(typed)) {
                builder.suggest(character.id());
            }
        }
        return builder.buildFuture();
    }
}