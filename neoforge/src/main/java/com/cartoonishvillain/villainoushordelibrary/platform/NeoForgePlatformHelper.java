package com.cartoonishvillain.villainoushordelibrary.platform;

import com.cartoonishvillain.villainoushordelibrary.NeoForgeVillainousHordeLibrary;
import com.cartoonishvillain.villainoushordelibrary.platform.services.IPlatformHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public Logger getLOGGER() {
        return NeoForgeVillainousHordeLibrary.LOGGER;
    }

    @Override
    public void finalizeSpawn(PathfinderMob mobToSpawn, ServerLevel world, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag) {
        EventHooks.onFinalizeSpawn(mobToSpawn, world, difficultyInstance, mobSpawnType, spawnData, spawnTag);
    }
}