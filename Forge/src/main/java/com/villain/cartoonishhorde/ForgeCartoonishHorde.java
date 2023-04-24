package com.villain.cartoonishhorde;


import com.villain.cartoonishhorde.hordes.EntityTypeHorde;
import com.villain.cartoonishhorde.enumhordetest.ForgeTestEnumHorde;
import com.villain.cartoonishhorde.enumhordetest.ForgeTestHordeDataClass;
import com.villain.cartoonishhorde.hordedata.EntityTypeHordeData;
import com.villain.cartoonishhorde.hordedata.EnumHordeData;
import com.villain.cartoonishhorde.platform.Services;
import com.villain.cartoonishhorde.testcommands.EntityEnumHordeCommand;
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
    public static ForgeTestEnumHorde forgeTestEnumHorde;
    
    public ForgeCartoonishHorde() {
        CommonCartoonishHorde.init();

        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            //Step 2 - Instantiate
            entityTypeHorde = new EntityTypeHorde(event.getServer());
            forgeTestEnumHorde = new ForgeTestEnumHorde(event.getServer());
            //This horde will consist of spiders, evokers, and creepers. Roughly equal quantities, but this is psuedo-randomized, so results may vary.
            entityTypeHorde.setHordeData(
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.SPIDER, Spider.class),
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.EVOKER, Evoker.class),
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.CREEPER, Creeper.class)
            );
            //This enum horde will consist of creepers, spiders, and vindicators in the overworld, and zombies, evokers, and skeletons in the nether.
            forgeTestEnumHorde.setHordeData(
                    new EnumHordeData<>(2, 1, 1, ForgeTestHordeDataClass.CREEPEROVERZOMBIENETHER),
                    new EnumHordeData<>(2, 1, 1, ForgeTestHordeDataClass.SPIDEROVEREVOKERNETHER),
                    new EnumHordeData<>(2, 1, 1, ForgeTestHordeDataClass.VINDICATOROVERSKELETONNETHER)
            );
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            if (!event.level.isClientSide && entityTypeHorde != null) {
                //Step 3 - Connect
                entityTypeHorde.tick();
                forgeTestEnumHorde.tick();
            }
        }
    }
    @SubscribeEvent
    public void CMDRegister(RegisterCommandsEvent event) {
        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            EntityTypeHordeCommand.register(event.getDispatcher());
            EntityEnumHordeCommand.register(event.getDispatcher());
        }
    }
}