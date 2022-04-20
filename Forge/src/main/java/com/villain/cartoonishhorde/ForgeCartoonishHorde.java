package com.villain.cartoonishhorde;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Giant;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ForgeCartoonishHorde {
    
    public ForgeCartoonishHorde() {
        CommonCartoonishHorde.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //Step 2 - Instantiate
        CommonCartoonishHorde.serverStart(event.getServer());
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if(!event.world.isClientSide && CommonCartoonishHorde.horde != null) {
            //Step 3 - Connect
            CommonCartoonishHorde.hordeTick();
        }
    }
}