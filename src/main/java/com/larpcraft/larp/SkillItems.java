package com.larpcraft.larp;

import java.util.List;
import java.util.Map;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillItems {

    // One skill item: which vanilla item it is, and the name we give it.
    private record Def(Item item, String name) {
    }

    // The order of each list is the skill slot: 1st = skill 1, 2nd = skill 2, 3rd = ultimate.
    private static final Map<String, List<Def>> ITEMS = Map.of(
            "gojo", List.of(
                    new Def(Items.AMETHYST_SHARD, "Blue"),
                    new Def(Items.ENDER_EYE, "Infinity"),
                    new Def(Items.NETHER_STAR, "Hollow Purple")
            ),
            "ayanokoji", List.of(
                    new Def(Items.BOOK, "Observe"),
                    new Def(Items.FERMENTED_SPIDER_EYE, "Manipulate"),
                    new Def(Items.ECHO_SHARD, "White Room")
            )
    );

    // Which skill slot (1, 2, 3) does this stack trigger for this character? 0 = none.
    public static int slotOf(String characterId, ItemStack stack) {
        List<Def> defs = ITEMS.get(characterId);
        if (defs == null) {
            return 0;
        }
        for (int i = 0; i < defs.size(); i++) {
            if (matches(defs.get(i), stack)) {
                return i + 1;
            }
        }
        return 0;
    }

    // Is this stack a skill item of ANY character?
    public static boolean isSkillItem(ItemStack stack) {
        for (List<Def> defs : ITEMS.values()) {
            for (Def def : defs) {
                if (matches(def, stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Gives the player the three named items for this character.
    public static void give(ServerPlayer player, String characterId) {
        List<Def> defs = ITEMS.get(characterId);
        if (defs == null) {
            return;
        }
        for (Def def : defs) {
            ItemStack stack = new ItemStack(def.item());
            stack.set(DataComponents.ITEM_NAME, Component.literal(def.name()));
            boolean added = player.getInventory().add(stack);
            if (!added) {
                player.sendSystemMessage(Component.literal("Inventory full, could not give: " + def.name()));
            }
        }
    }

    // Removes every skill item (of any character) from the player's inventory.
    public static void removeAll(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (isSkillItem(inventory.getItem(i))) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static boolean matches(Def def, ItemStack stack) {
        if (!stack.is(def.item())) {
            return false;
        }
        Component name = stack.get(DataComponents.ITEM_NAME);
        return name != null && name.getString().equals(def.name());
    }
}