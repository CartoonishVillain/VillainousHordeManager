package com.villain.cartoonishhorde;


import com.villain.cartoonishhorde.hordedata.EntityTypeHordeData;
import com.villain.cartoonishhorde.platform.Services;
import com.villain.cartoonishhorde.testcommands.EntityTypeHordeCommand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Spider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ForgeCartoonishHorde {

    public static EntityTypeHorde entityTypeHorde;
    
    public ForgeCartoonishHorde() {
        CommonCartoonishHorde.init();

        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //Step 2 - Instantiate
        entityTypeHorde = new EntityTypeHorde(event.getServer());
        //This horde will consist of spiders, evokers, and creepers. Roughly equal quantities, but this is psuedo-randomized, so results may vary.
        entityTypeHorde.setHordeData(
                new EntityTypeHordeData<>(2, 1, 1, EntityType.SPIDER, Spider.class),
                new EntityTypeHordeData<>(2, 1, 1, EntityType.EVOKER, Evoker.class),
                new EntityTypeHordeData<>(2, 1, 1, EntityType.CREEPER, Creeper.class));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if(!event.level.isClientSide && entityTypeHorde != null) {
            //Step 3 - Connect
            entityTypeHorde.tick();
        }
    }
    @SubscribeEvent
    public void CMDRegister(RegisterCommandsEvent event) {
        EntityTypeHordeCommand.register(event.getDispatcher());
    }
}