package com.villain.cartoonishhorde.hordedata;

import com.villain.cartoonishhorde.RuleEnumInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

/*
    Used to store data about horde members in a map. Including spawn weight and goal priority to move towards the center.
 */
public class EnumHordeData<T extends PathfinderMob> implements HordeData {
    private final RuleEnumInterface type;
    private final double goalMovementSpeed;
    private final int goalPriority;
    private final int spawnWeight;

    public EnumHordeData(int goalPriority, double goalMovementSpeed, int spawnWeight, RuleEnumInterface type){
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = type;
    }

    public double getGoalMovementSpeed() {
        return goalMovementSpeed;
    }

    public int getGoalPriority() {
        return goalPriority;
    }

    public int getSpawnWeight() {
        return spawnWeight;
    }

    public RuleEnumInterface getType() {
        return type;
    }
}
