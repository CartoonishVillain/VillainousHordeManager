package com.villain.cartoonishhorde.mixin;

import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(GoalSelector.class)
public interface AvailableGoalsAccessor
{
    @Accessor("availableGoals")
    Set<WrappedGoal> cartoonishHordeGetAvailableGoals();
}