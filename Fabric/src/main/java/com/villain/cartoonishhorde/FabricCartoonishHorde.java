package com.villain.cartoonishhorde;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Giant;

public class FabricCartoonishHorde implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonCartoonishHorde.init();
        ServerLifecycleEvents.SERVER_STARTING.register(ServerStartListener.getInstance());
        ServerTickEvents.END_WORLD_TICK.register(WorldTickListener.getInstance());
    }

    public static class ServerStartListener implements ServerLifecycleEvents.ServerStarting{
        private static final ServerStartListener INSTANCE = new ServerStartListener();

        public static ServerStartListener getInstance() {return INSTANCE;}

        @Override
        public void onServerStarting(MinecraftServer server) {
            //Step 2 - Instantiate
            CommonCartoonishHorde.serverStart(server);
        }
    }

    public static class WorldTickListener implements ServerTickEvents.EndWorldTick{
        private static final WorldTickListener INSTANCE = new WorldTickListener();

        public static WorldTickListener getInstance() {return INSTANCE;}

        @Override
        public void onEndTick(ServerLevel world) {
            //Step 3 - Connect
            CommonCartoonishHorde.hordeTick();
        }
    }
}
