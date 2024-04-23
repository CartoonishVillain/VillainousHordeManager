package com.cartoonishvillain.villainoushordelibrary.hordedata;

import com.cartoonishvillain.villainoushordelibrary.data.JsonEffectData;
import com.cartoonishvillain.villainoushordelibrary.data.JsonNBTData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

import java.util.ArrayList;

/*
    Used to store data about horde members in a map. Including spawn weight and goal priority to move towards the center.
 */
public class EntityTypeHordeData<T extends PathfinderMob> implements HordeData {
    private final EntityType<? extends PathfinderMob> type;
    private final double goalMovementSpeed;
    private final int goalPriority;
    private final int spawnWeight;
    private final ArrayList<JsonNBTData> nbtData;

    public EntityTypeHordeData(int goalPriority, double goalMovementSpeed, int spawnWeight, EntityType<T> type){
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = type;
        nbtData = null;
    }

    public EntityTypeHordeData(int goalPriority, double goalMovementSpeed, int spawnWeight, EntityType<T> type, ArrayList<JsonNBTData> nbtData){
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = type;
        this.nbtData = nbtData;
    }

    public EntityTypeHordeData(Integer goalPriority, Integer goalMovementSpeed, Integer spawnWeight, EntityType<? extends PathfinderMob> entityType) {
        this.goalPriority = goalPriority;
        this.goalMovementSpeed = goalMovementSpeed;
        this.spawnWeight = spawnWeight;
        this.type = entityType;
        nbtData = null;
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

    public ArrayList<JsonNBTData> getNbtData() {
        return nbtData;
    }

    public T createInstance(ServerLevel level) {
        ArrayList<MobEffectInstance> effectsToAdd = null;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", EntityType.getKey(type).getPath());
        if (nbtData != null) {
            for (JsonNBTData tagData : nbtData) {
                if (tagData.getType().equalsIgnoreCase("effect")) {
                    if (tagData.getEffectData() != null) {
                        effectsToAdd = new ArrayList<>();
                        for (JsonEffectData effectData : tagData.getEffectData()) {
                            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(effectData.getEffect()));

                            if (effect != null) {
                                effectsToAdd.add(
                                        new MobEffectInstance(
                                                effect,
                                                Integer.parseInt(effectData.getDuration()),
                                                Integer.parseInt(effectData.getAmplifier()),
                                                effectData.isShowParticles(),
                                                effectData.isShowParticles()
                                        )
                                );
                            }
                        }
                    }
                } else {
                    addNBTData(tagData, compoundTag);
                }
            }
        }
        ArrayList<MobEffectInstance> finalEffectsToAdd = effectsToAdd;
        return (T) EntityType.loadEntityRecursive(compoundTag, level, (entityx) -> {
            if (finalEffectsToAdd != null) {
                for (MobEffectInstance effectInstance : finalEffectsToAdd) {
                    ((LivingEntity) entityx).addEffect(effectInstance);
                }
            }

            return entityx;
        });
    }

    public void addNBTData(JsonNBTData data, CompoundTag compoundTag) {
        switch (data.getType().toLowerCase()) {
            case "string" -> compoundTag.putString(data.getKey(), data.getValue());

            case "int" -> compoundTag.putInt(data.getKey(), Integer.parseInt(data.getValue()));

            case "float" -> compoundTag.putFloat(data.getKey(), Float.parseFloat(data.getValue()));

            case "double" -> compoundTag.putDouble(data.getKey(), Double.parseDouble(data.getValue()));

            case "byte" -> compoundTag.putByte(data.getKey(), Byte.parseByte(data.getValue()));

            case "boolean" -> compoundTag.putBoolean(data.getKey(), Boolean.parseBoolean(data.getValue()));

            case "short" -> compoundTag.putShort(data.getKey(), Short.parseShort(data.getValue()));

            default -> {

            }
        }
    }
}
