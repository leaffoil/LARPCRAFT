package com.larpcraft.larp;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LARPCRAFT implements ModInitializer {
	public static final String MOD_ID = "larpcraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		LarpCommand.register();
		PassiveAbilities.register();
		ActiveAbilities.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}