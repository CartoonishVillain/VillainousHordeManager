package com.cartoonishvillain.villainoushordemanager;


import com.cartoonishvillain.villainoushordemanager.codebasedhordetest.TestEnumHorde;
import com.cartoonishvillain.villainoushordemanager.codebasedhordetest.TestHordeDataClass;
import com.cartoonishvillain.villainoushordemanager.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordemanager.hordedata.EnumHordeData;
import com.cartoonishvillain.villainoushordemanager.hordes.EntityTypeHorde;
import com.cartoonishvillain.villainoushordemanager.platform.Services;
import com.cartoonishvillain.villainoushordemanager.testcommands.EntityEnumHordeCommand;
import com.cartoonishvillain.villainoushordemanager.testcommands.EntityJsonHordeCommand;
import com.cartoonishvillain.villainoushordemanager.testcommands.EntityTypeHordeCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.slf4j.Logger;

import static com.cartoonishvillain.villainoushordemanager.VillainousHordeManager.*;

@Mod(Constants.MOD_ID)
public class NeoForgeVillainousHordeManager {

    public static final Logger LOGGER = LogUtils.getLogger();

    public NeoForgeVillainousHordeManager(IEventBus eventBus) {
        NeoForge.EVENT_BUS.register(this);
        VillainousHordeManager.init();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            //Step 1 - Instantiate
            VillainousHordeManager.entityTypeHorde = new EntityTypeHorde(event.getServer());
            VillainousHordeManager.entityEnumHorde = new TestEnumHorde(event.getServer());
            //This horde will consist of spiders, evokers, and creepers. Roughly equal quantities, but this is psuedo-randomized, so results may vary.
            VillainousHordeManager.entityTypeHorde.setHordeData(
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.SPIDER),
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.EVOKER),
                    new EntityTypeHordeData<>(2, 1, 1, EntityType.CREEPER)
            );
            //This enum horde will consist of creepers, spiders, and vindicators in the overworld, and zombies, evokers, and skeletons in the nether.
            VillainousHordeManager.entityEnumHorde.setHordeData(
                    new EnumHordeData(2, 1, 1, TestHordeDataClass.CREEPEROVERZOMBIENETHER),
                    new EnumHordeData(2, 1, 1, TestHordeDataClass.SPIDEROVEREVOKERNETHER),
                    new EnumHordeData(2, 1, 1, TestHordeDataClass.VINDICATOROVERSKELETONNETHER)
            );
        }

        loadHordes();
    }

    @SubscribeEvent
    public void onEntityDamaged(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof PathfinderMob && isHordeMember((PathfinderMob) event.getEntity()) && event.getEntity().tickCount < 2) {
            event.getEntity().remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @SubscribeEvent
    public void onWorldTick(LevelTickEvent.Pre event) {
        if(Services.PLATFORM.isDevelopmentEnvironment()) {
            if (!event.getLevel().isClientSide && VillainousHordeManager.entityTypeHorde != null) {
                //Step 2 - Connect
                VillainousHordeManager.entityTypeHorde.tick();
            }
            
            if (!event.getLevel().isClientSide && VillainousHordeManager.entityEnumHorde != null) {
                VillainousHordeManager.entityEnumHorde.tick();
            }
        }

        if (!event.getLevel().isClientSide && jsonHorde != null) {
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