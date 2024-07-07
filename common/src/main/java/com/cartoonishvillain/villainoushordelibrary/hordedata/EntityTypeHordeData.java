package com.cartoonishvillain.villainoushordelibrary.hordedata;

import com.cartoonishvillain.villainoushordelibrary.Constants;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
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
    private final String nbtData;

    public EntityTypeHordeData(int goalPriority, double goalMovementSpeed, int spawnWeight, EntityType<T> type){
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = type;
        nbtData = null;
    }

    public EntityTypeHordeData(int goalPriority, double goalMovementSpeed, int spawnWeight, EntityType<T> type, String nbtData){
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = type;
        this.nbtData = nbtData;
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
        CompoundTag compoundTag = new CompoundTag();
        if (nbtData != null) {
            try {
                compoundTag = TagParser.parseTag(nbtData);
            } catch (CommandSyntaxException e) {
                Constants.LOG.error("Horde Manager - Failed to load NBT data for: {}", EntityType.getKey(type).getPath());
            }
        }
        compoundTag.putString("id", EntityType.getKey(type).getPath());

        return (T) EntityType.loadEntityRecursive(compoundTag, level, (entityx) -> entityx);
    }
}
