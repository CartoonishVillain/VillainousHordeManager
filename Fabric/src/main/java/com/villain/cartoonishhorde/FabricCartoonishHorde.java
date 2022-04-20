package com.villain.cartoonishhorde;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class FabricCartoonishHorde implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonCartoonishHorde.init();
    }
}
