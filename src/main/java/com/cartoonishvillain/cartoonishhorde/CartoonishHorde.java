package com.cartoonishvillain.cartoonishhorde;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cartoonishhorde")
public class CartoonishHorde
{
    //Horde horde;
    public static final String MOD_ID = "cartoonishhorde";


    public CartoonishHorde() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }


    public static boolean isHordeMember(PathfinderMob entity) {
        Set<WrappedGoal> prioritizedGoals = ObfuscationReflectionHelper.getPrivateValue(GoalSelector.class, entity.goalSelector, "f_25345_");
        Goal hordeGoal = null;
        if (prioritizedGoals != null) {
            for (WrappedGoal prioritizedGoal : prioritizedGoals) {
                if (prioritizedGoal.getGoal() instanceof HordeMovementGoal) {
                    hordeGoal = prioritizedGoal.getGoal();
                    if (hordeGoal != null) break;
                }
            }
            return hordeGoal != null;
        }
        return false;
    }

    //Test code.
//    @SubscribeEvent
//    public void onServerStarting(ServerStartingEvent event) {
//        //Step 2 - Instantiate
//        horde = new Horde(event.getServer());
//        //This horde will consist of spiders, evokers, and creepers. Roughly equal quantities, but this is psuedo-randomized, so results may vary.
//        horde.setHordeData(new EntityHordeData<>(2, 1, 1, EntityType.GIANT, Giant.class));
//    }
//
//    @SubscribeEvent
//    public void onWorldTick(TickEvent.WorldTickEvent event) {
//        if(!event.world.isClientSide && horde != null) {
//            //Step 3 - Connect
//            horde.tick();
//        }
//    }
//
//    @SubscribeEvent
//    public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
//        if(event.getEntityLiving() instanceof Player && !event.getEntityLiving().level.isClientSide && horde != null && !horde.getHordeActive()) {
//            //Step 4 - Start.
//            //This will start this test horde whenever it is not ongoing an a player jumps.
//            horde.SetUpHorde((ServerPlayer) event.getEntityLiving());
//        }
//    }
}
