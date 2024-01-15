package com.cartoonishvillain.villainoushordelibrary;


import com.cartoonishvillain.villainoushordelibrary.codebasedhordetest.TestEnumHorde;
import com.cartoonishvillain.villainoushordelibrary.codebasedhordetest.TestHordeDataClass;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EnumHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityTypeHorde;
import com.cartoonishvillain.villainoushordelibrary.platform.Services;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityEnumHordeCommand;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityJsonHordeCommand;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityTypeHordeCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.io.FileNotFoundException;

import static com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary.*;

@Mod(Constants.MOD_ID)
public class NeoForgeVillainousHordeLibrary {

    public static final Logger LOGGER = LogUtils.getLogger();

    public NeoForgeVillainousHordeLibrary(IEventBus eventBus) {
        NeoForge.EVENT_BUS.register(this);
        VillainousHordeLibrary.init();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            //Step 1 - Instantiate
            VillainousHordeLibrary.entityTypeHorde = new EntityTypeHorde(event.getServer());
            VillainousHordeLibrary.entityEnumHorde = new TestEnumHorde(event.getServer());
            //This horde will consist of spiders, evokers, and creepers. Roughly equal quantities, but this is psuedo-randomized, so results may vary.
            VillainousHordeLibrary.entityTypeHorde.setHordeData(
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.SPIDER),
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.EVOKER),
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.CREEPER)
            );
            //This enum horde will consist of creepers, spiders, and vindicators in the overworld, and zombies, evokers, and skeletons in the nether.
            VillainousHordeLibrary.entityEnumHorde.setHordeData(
                    new EnumHordeData(2, 1, 1, TestHordeDataClass.CREEPEROVERZOMBIENETHER),
                    new EnumHordeData(2, 1, 1, TestHordeDataClass.SPIDEROVEREVOKERNETHER),
                    new EnumHordeData(2, 1, 1, TestHordeDataClass.VINDICATOROVERSKELETONNETHER)
            );
        }

        try {
            loadHordes();
        } catch (FileNotFoundException e) {
            LOGGER.warn("VillainousHordeLibrary - hordeJsonData.json not found! No Json hordes are loaded!");
        }
    }

    @SubscribeEvent
    public void onEntityDamaged(LivingDamageEvent event) {
        if (event.getEntity() instanceof PathfinderMob && isHordeMember((PathfinderMob) event.getEntity()) && event.getEntity().tickCount < 2) {
            event.getEntity().remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            if (!event.level.isClientSide && VillainousHordeLibrary.entityTypeHorde != null) {
                //Step 2 - Connect
                VillainousHordeLibrary.entityTypeHorde.tick();
            }
            
            if (!event.level.isClientSide && VillainousHordeLibrary.entityEnumHorde != null) {
                VillainousHordeLibrary.entityEnumHorde.tick();
            }
        }

        if (!event.level.isClientSide && jsonHorde != null) {
            jsonHorde.tick();
        }
    }

    @SubscribeEvent
    public void CMDRegister(RegisterCommandsEvent event) {
        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            EntityTypeHordeCommand.register(event.getDispatcher());
            EntityEnumHordeCommand.register(event.getDispatcher());
        }
        EntityJsonHordeCommand.register(event.getDispatcher());
    }
}