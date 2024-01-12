package com.cartoonishvillain.villainoushordelibrary.hordedata;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

/*
    Used to store data about horde members in a map. Including spawn weight and goal priority to move towards the center.
 */
public class EntityTypeHordeData<T extends PathfinderMob> implements HordeData {
    private final EntityType<? extends PathfinderMob> type;
    private final double goalMovementSpeed;
    private final int goalPriority;
    private final int spawnWeight;

    public EntityTypeHordeData(int goalPriority, double goalMovementSpeed, int spawnWeight, EntityType<T> type){
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = type;
    }

    public EntityTypeHordeData(Integer goalPriority, Integer goalMovementSpeed, Integer spawnWeight, EntityType<? extends PathfinderMob> entityType) {
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = entityType;
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

    public EntityType<? extends PathfinderMob> getType() {
        return type;
    }

    public T createInstance(ServerLevel level) {
        return (T) type.create(level);
    }
}
