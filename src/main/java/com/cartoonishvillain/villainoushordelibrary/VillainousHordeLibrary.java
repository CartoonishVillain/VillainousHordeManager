package com.cartoonishvillain.villainoushordelibrary;

import com.cartoonishvillain.villainoushordelibrary.codebasedhordetest.ForgeTestEnumHorde;
import com.cartoonishvillain.villainoushordelibrary.codebasedhordetest.ForgeTestHordeDataClass;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EnumHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityTypeHorde;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityEnumHordeCommand;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityTypeHordeCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Spider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.Set;
@Mod(VillainousHordeLibrary.MODID)
public class VillainousHordeLibrary
{
    public static EntityTypeHorde entityTypeHorde;
    public static ForgeTestEnumHorde forgeTestEnumHorde;

    // Define mod id in a common place for everything to reference
    public static final String MODID = "villainoushordelibrary";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public VillainousHordeLibrary(IEventBus modEventBus)
    {
        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        if (!FMLLoader.isProduction()) {
            //Step 1 - Instantiate
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
        if(!FMLLoader.isProduction()) {
            if (!event.level.isClientSide && entityTypeHorde != null) {
                //Step 2 - Connect
                entityTypeHorde.tick();
                forgeTestEnumHorde.tick();
            }
        }
    }

    @SubscribeEvent
    public void CMDRegister(RegisterCommandsEvent event) {
        if(!FMLLoader.isProduction()) {
            EntityTypeHordeCommand.register(event.getDispatcher());
            EntityEnumHordeCommand.register(event.getDispatcher());
        }
    }

    public static boolean isHordeMember(PathfinderMob entity) {
        GoalSelector mobGoalSelector = entity.goalSelector;
        Set<WrappedGoal> prioritizedGoals = mobGoalSelector.getAvailableGoals();
        Goal hordeGoal = null;
        for (WrappedGoal prioritizedGoal : prioritizedGoals) {
            if (prioritizedGoal.getGoal() instanceof TypeHordeMovementGoal || prioritizedGoal.getGoal() instanceof EnumHordeMovementGoal) {
                hordeGoal = prioritizedGoal.getGoal();
                break;
            }
        }
        return hordeGoal != null;
    }
}
