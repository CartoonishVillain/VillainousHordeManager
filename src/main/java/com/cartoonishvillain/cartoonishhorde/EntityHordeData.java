package com.cartoonishvillain.cartoonishhorde;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

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
        T instance = null;
        try {
            Constructor<T>[] constructors = (Constructor<T>[]) entityClass.getDeclaredConstructors();
            Constructor<T> constructor = null;
            for (Constructor<T> cstr : constructors) {
                ArrayList<Parameter> parameters = new ArrayList<>(List.of(cstr.getParameters()));
                if (parameters.size() == 2) {
                    constructor = cstr;
                    break;
                }
            }
            if (constructor != null) {
                constructor.setAccessible(true);
                instance = constructor.newInstance(type, level);
            }

            return instance;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
