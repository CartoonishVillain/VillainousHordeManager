package com.villain.cartoonishhorde;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

/*
    Used to store data about horde members in a map. Including spawn weight and goal priority to move towards the center.
 */
public class EntityHordeData<T extends PathfinderMob> {
    private final int goalPriority;
    private final int SpawnWeight;
    private final EntityType type;
    private final Class<T> entityClass;
    private final double goalMovementSpeed;

    public EntityHordeData(int goalPriority, double goalMovementSpeed, int SpawnWeight, EntityType type, Class<T> entityClass){
        this.goalPriority = goalPriority;
        this.SpawnWeight = SpawnWeight;
        this.type = type;
        this.entityClass = entityClass;
        this.goalMovementSpeed = goalMovementSpeed;
    }

    public double getGoalMovementSpeed() {
        return goalMovementSpeed;
    }

    public int getGoalPriority() {
        return goalPriority;
    }

    public int getSpawnWeight() {
        return SpawnWeight;
    }

    public EntityType getType() {
        return type;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public T createInstance(ServerLevel level) {
        T instance = (T) type.create(level);
        return instance;
    }
}
