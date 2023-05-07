package com.villain.villainoushordemanager;

import net.fabricmc.api.ModInitializer;

public class FabricVillainousHordeManager implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonVillainousHordeManager.init();
    }
}
