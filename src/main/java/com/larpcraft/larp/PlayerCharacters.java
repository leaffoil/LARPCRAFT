package com.larpcraft.larp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerCharacters {

    private static final Map<UUID, String> SELECTED = new HashMap<>();

    public static void set(UUID playerId, String characterId) {
        SELECTED.put(playerId, characterId);
    }

    public static Optional<String> get(UUID playerId) {
        return Optional.ofNullable(SELECTED.get(playerId));
    }
}