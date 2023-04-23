package com.villain.cartoonishhorde.hordedata;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

/*
    Used to store data about horde members in a map. Including spawn weight and goal priority to move towards the center.
 */
public class EntityTypeHordeData<T extends PathfinderMob> implements HordeData {
    private final EntityType<T> type;
    private final Class<T> entityClass;
    private final double goalMovementSpeed;
    private final int goalPriority;
    private final int spawnWeight;

    public EntityTypeHordeData(int goalPriority, double goalMovementSpeed, int spawnWeight, EntityType<T> type, Class<T> entityClass){
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = type;
        this.entityClass = entityClass;
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

    public EntityType<T> getType() {
        return type;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public T createInstance(ServerLevel level) {
        return (T) type.create(level);
    }
}
