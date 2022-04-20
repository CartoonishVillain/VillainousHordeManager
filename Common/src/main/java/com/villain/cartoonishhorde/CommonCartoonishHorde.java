package com.villain.cartoonishhorde;

import com.villain.cartoonishhorde.mixin.AvailableGoalsAccessor;
import com.villain.cartoonishhorde.mixin.LivingGoalAccessor;
import com.villain.cartoonishhorde.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class CommonCartoonishHorde {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.

    public static Horde horde;

    public static void init() {

    }

    public static void serverStart(MinecraftServer server) {
        //Step 2 - Instantiate
        horde = new Horde(server);
        horde.setHordeData(new EntityHordeData<>(2, 1, 1, EntityType.SPIDER, Giant.class));
    }

    public static void hordeTick() {
        //Step 3 - Connect
        horde.tick();
    }

    //STEP 4 is in JumpMixin

    public static void jumpBoi(ServerPlayer player) {
        horde.SetUpHorde(player);
    }

    public static boolean isHordeMember(PathfinderMob entity) {
        GoalSelector mobGoalSelector = ((LivingGoalAccessor) entity).cartoonishHordeGetMobGoalSelector();
        Set<WrappedGoal> prioritizedGoals = ((AvailableGoalsAccessor) mobGoalSelector).cartoonishHordeGetAvailableGoals();
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

}