package com.cartoonishvillain.villainoushordelibrary.hordedata;

import com.cartoonishvillain.villainoushordelibrary.data.JsonAttributeData;
import com.cartoonishvillain.villainoushordelibrary.data.JsonEffectData;
import com.cartoonishvillain.villainoushordelibrary.data.JsonNBTData;
import com.google.common.collect.Multimap;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Zombie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        HashMap<Attribute, AttributeModifier> attributeMap = null;
        ArrayList<MobEffectInstance> effectsToAdd = null;
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", EntityType.getKey(type).getPath());
        if (nbtData != null) {
            for (JsonNBTData tagData : nbtData) {
                if (tagData.getType().equalsIgnoreCase("effect")) {
                    if (tagData.getEffectData() != null) {
                        effectsToAdd = new ArrayList<>();
                        addEffect(tagData.getEffectData(), effectsToAdd);
                    }
                } else if (tagData.getType().equalsIgnoreCase("attribute")) {
                    if (tagData.getAttributeData() != null) {
                        attributeMap = new HashMap<>();
                        addAttributes(tagData.getAttributeData(), attributeMap);
                    }
                }

                else {
                    addNBTData(tagData, compoundTag);
                }
            }
        }
        ArrayList<MobEffectInstance> finalEffectsToAdd = effectsToAdd;
        HashMap<Attribute, AttributeModifier> finalAttributeMap = attributeMap;
        return (T) EntityType.loadEntityRecursive(compoundTag, level, (entityx) -> {
            if (finalEffectsToAdd != null) {
                for (MobEffectInstance effectInstance : finalEffectsToAdd) {
                    ((LivingEntity) entityx).addEffect(effectInstance);
                }
            }

            if (finalAttributeMap != null) {
                for (Map.Entry<Attribute, AttributeModifier> modifierEntry : finalAttributeMap.entrySet()) {
                    AttributeInstance instance = ((LivingEntity) entityx).getAttribute(modifierEntry.getKey());
                    instance.addTransientModifier(modifierEntry.getValue());
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

    public void addEffect(ArrayList<JsonEffectData> effectDatas, ArrayList<MobEffectInstance> effectInstances) {
        for (JsonEffectData effectData : effectDatas) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(effectData.getEffect()));

            if (effect != null) {
                effectInstances.add(
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

    public void addAttributes(ArrayList<JsonAttributeData> attributeDatas, HashMap<Attribute, AttributeModifier> map) {
        for (JsonAttributeData attributeData : attributeDatas) {
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(attributeData.getAttributeID()));
            map.put(attribute, new AttributeModifier(
                    attributeData.getModifierName(), Double.parseDouble(attributeData.getModifierAmount()), AttributeModifier.Operation.fromValue(Integer.parseInt(attributeData.getModifierOperation()))
            ));
        }
    }
}
