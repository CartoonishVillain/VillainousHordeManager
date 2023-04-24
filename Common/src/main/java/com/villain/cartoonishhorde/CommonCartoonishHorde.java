package com.villain.cartoonishhorde;

import com.villain.cartoonishhorde.mixin.AvailableGoalsAccessor;
import com.villain.cartoonishhorde.mixin.LivingGoalAccessor;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.util.Set;

public class CommonCartoonishHorde {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {
    }

    public static boolean isHordeMember(PathfinderMob entity) {
        GoalSelector mobGoalSelector = ((LivingGoalAccessor) entity).cartoonishHordeGetMobGoalSelector();
        Set<WrappedGoal> prioritizedGoals = ((AvailableGoalsAccessor) mobGoalSelector).cartoonishHordeGetAvailableGoals();
        Goal hordeGoal = null;
        if (prioritizedGoals != null) {
            for (WrappedGoal prioritizedGoal : prioritizedGoals) {
                if (prioritizedGoal.getGoal() instanceof TypeHordeMovementGoal) {
                    hordeGoal = prioritizedGoal.getGoal();
                    if (hordeGoal != null) break;
                }
            }
            return hordeGoal != null;
        }
        return false;
    }

}