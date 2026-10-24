package com.larpcraft.larp;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class PassiveAbilities {

    // Effects last 300 ticks (15 seconds) and are refreshed every 100 ticks (5 seconds).
    // Minecraft runs 20 ticks per second.
    private static final int DURATION = 300;
    private static final int REFRESH_INTERVAL = 100;

    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % REFRESH_INTERVAL != 0) {
                return;
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                PlayerCharacters.get(player.getUUID()).ifPresent(id -> apply(player, id));
            }
        });
    }

    public static int currentTick() {
        return tickCounter;
    }

    public static void apply(ServerPlayer player, String characterId) {
        switch (characterId) {
            case "gojo" -> {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, DURATION, 1, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, DURATION, 0, true, false, true));
            }
            case "ayanokoji" -> {
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, DURATION, 1, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, DURATION, 0, true, false, true));
            }
            default -> {
            }
        }
    }
}