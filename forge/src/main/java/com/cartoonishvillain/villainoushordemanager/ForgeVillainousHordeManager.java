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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.FileNotFoundException;

import static com.cartoonishvillain.villainoushordemanager.VillainousHordeManager.*;
import static com.cartoonishvillain.villainoushordemanager.VillainousHordeManager.jsonHorde;

@Mod(Constants.MOD_ID)
public class ForgeVillainousHordeManager {

    public static final Logger LOGGER = LogUtils.getLogger();
    public ForgeVillainousHordeManager() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        VillainousHordeManager.init();
        MinecraftForge.EVENT_BUS.register(this);
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

        try {
            loadHordes();
        } catch (FileNotFoundException e) {
            ForgeVillainousHordeManager.LOGGER.warn("VillainousHordeManager - hordeJsonData.json not found! No Json hordes are loaded!");
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
            if (!event.level.isClientSide && VillainousHordeManager.entityTypeHorde != null) {
                //Step 2 - Connect
                VillainousHordeManager.entityTypeHorde.tick();
            }

            if (!event.level.isClientSide && VillainousHordeManager.entityEnumHorde != null) {
                VillainousHordeManager.entityEnumHorde.tick();
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