package com.larpcraft.larp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ActiveAbilities {

    // Cooldowns in ticks (20 ticks = 1 second)
    private static final int COOLDOWN_SKILL_1 = 400;   // 20 seconds
    private static final int COOLDOWN_SKILL_2 = 600;   // 30 seconds
    private static final int COOLDOWN_ULTIMATE = 2400; // 120 seconds

    // "playerUUID:slot" -> server tick when that skill was last used
    private static final Map<String, Integer> LAST_USED = new HashMap<>();

    public static void register() {
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.isSpectator()) {
                return InteractionResult.PASS;
            }

            Optional<String> chosen = PlayerCharacters.get(serverPlayer.getUUID());
            if (chosen.isEmpty()) {
                return InteractionResult.PASS;
            }
            String characterId = chosen.get();

            ItemStack stack = serverPlayer.getItemInHand(hand);
            int slot = skillSlot(characterId, stack);
            if (slot == 0) {
                return InteractionResult.PASS;
            }

            String key = serverPlayer.getUUID() + ":" + slot;
            int cooldown = cooldownTicks(slot);
            int now = PassiveAbilities.currentTick();
            Integer last = LAST_USED.get(key);
            if (last != null && now - last < cooldown) {
                int secondsLeft = (cooldown - (now - last)) / 20 + 1;
                serverPlayer.sendSystemMessage(Component.literal("Skill on cooldown: " + secondsLeft + "s"));
                return InteractionResult.FAIL;
            }

            LAST_USED.put(key, now);
            activate(serverPlayer, level, characterId, slot);
            return InteractionResult.SUCCESS;
        });
    }

    // Which skill (1, 2 or 3 = ultimate) does this item trigger? 0 = none.
    private static int skillSlot(String characterId, ItemStack stack) {
        return switch (characterId) {
            case "gojo" -> stack.is(Items.AMETHYST_SHARD) ? 1
                    : stack.is(Items.ENDER_EYE) ? 2
                    : stack.is(Items.NETHER_STAR) ? 3 : 0;
            case "ayanokoji" -> stack.is(Items.BOOK) ? 1
                    : stack.is(Items.FERMENTED_SPIDER_EYE) ? 2
                    : stack.is(Items.ECHO_SHARD) ? 3 : 0;
            default -> 0;
        };
    }

    private static int cooldownTicks(int slot) {
        return switch (slot) {
            case 1 -> COOLDOWN_SKILL_1;
            case 2 -> COOLDOWN_SKILL_2;
            default -> COOLDOWN_ULTIMATE;
        };
    }

    private static void activate(ServerPlayer player, Level level, String characterId, int slot) {
        switch (characterId) {
            case "gojo" -> gojo(player, level, slot);
            case "ayanokoji" -> ayanokoji(player, level, slot);
            default -> {
            }
        }
    }

    private static void gojo(ServerPlayer player, Level level, int slot) {
        switch (slot) {
            case 1 -> { // Blue
                for (LivingEntity target : nearby(player, level, 8)) {
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3, false, true, true));
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false, true));
                }
                say(player, "Cursed Technique Lapse: Blue!");
            }
            case 2 -> { // Infinity
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 4, false, true, true));
                say(player, "Infinity!");
            }
            case 3 -> { // Hollow Purple
                for (LivingEntity target : nearby(player, level, 10)) {
                    target.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 2, false, true, true));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, true, true));
                }
                say(player, "Hollow Purple!");
            }
            default -> {
            }
        }
    }

    private static void ayanokoji(ServerPlayer player, Level level, int slot) {
        switch (slot) {
            case 1 -> { // Observe
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 2, false, true, true));
                say(player, "Observe...");
            }
            case 2 -> { // Manipulate
                for (LivingEntity target : nearby(player, level, 8)) {
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, true, true));
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 1, false, true, true));
                }
                say(player, "Everything is going as planned.");
            }
            case 3 -> { // White Room
                for (LivingEntity target : nearby(player, level, 12)) {
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0, false, true, true));
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 2, false, true, true));
                }
                player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 3, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 0, false, true, true));
                say(player, "Welcome to the White Room.");
            }
            default -> {
            }
        }
    }

    // All living things within `radius` blocks of the player, except the player.
    private static List<LivingEntity> nearby(ServerPlayer player, Level level, double radius) {
        return level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                entity -> entity != player
        );
    }

    private static void say(ServerPlayer player, String text) {
        player.sendSystemMessage(Component.literal(text));
    }

    // Text shown to the player when they pick a character.
    public static String describe(String characterId) {
        return switch (characterId) {
            case "gojo" -> "Passive: Six Eyes (always on)\n"
                    + "Skill 1 (Amethyst Shard): Blue\n"
                    + "Skill 2 (Ender Eye): Infinity\n"
                    + "Ultimate (Nether Star): Hollow Purple";
            case "ayanokoji" -> "Passive: Observer (always on)\n"
                    + "Skill 1 (Book): Observe\n"
                    + "Skill 2 (Fermented Spider Eye): Manipulate\n"
                    + "Ultimate (Echo Shard): White Room";
            default -> "";
        };
    }
}