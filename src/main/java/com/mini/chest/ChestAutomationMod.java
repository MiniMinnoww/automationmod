package com.mini.chest;

import net.fabricmc.api.ModInitializer;

public class ChestAutomationMod implements ModInitializer {
    public static final String MODID = "chest";
    private static Macro macroInstance;
    @Override
    public void onInitialize() {
        macroInstance = new Macro();
    }
}