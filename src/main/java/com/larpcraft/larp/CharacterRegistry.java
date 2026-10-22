package com.larpcraft.larp;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CharacterRegistry {

    private static final Map<String, LarpCharacter> CHARACTERS = new LinkedHashMap<>();

    static {
        add(new LarpCharacter(
                "gojo",
                "Satoru Gojo",
                "A confident sorcerer who bends space around him."
        ));
        add(new LarpCharacter(
                "ayanokoji",
                "Kiyotaka Ayanokoji",
                "A calm, calculating observer who stays one step ahead."
        ));
    }

    private static void add(LarpCharacter character) {
        CHARACTERS.put(character.id(), character);
    }

    public static Optional<LarpCharacter> get(String id) {
        return Optional.ofNullable(CHARACTERS.get(id.toLowerCase()));
    }

    public static Collection<LarpCharacter> all() {
        return CHARACTERS.values();
    }
}