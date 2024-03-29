package com.cartoonishvillain.villainoushordelibrary.hordedata;

import com.cartoonishvillain.villainoushordelibrary.RuleEnumInterface;

/*
    Used to store data about horde members in a map. Including spawn weight and goal priority to move towards the center.
 */
public class EnumHordeData implements HordeData {
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
