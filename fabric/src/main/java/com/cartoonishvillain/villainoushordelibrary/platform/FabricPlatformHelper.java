package com.cartoonishvillain.villainoushordelibrary.platform;

import com.cartoonishvillain.villainoushordelibrary.FabricVillainousHordeLibrary;
import com.cartoonishvillain.villainoushordelibrary.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Logger getLOGGER() {
        return FabricVillainousHordeLibrary.LOGGER;
    }

    @Override
    public void finalizeSpawn(PathfinderMob mobToSpawn, ServerLevel world, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag) {
        mobToSpawn.finalizeSpawn(world, difficultyInstance, mobSpawnType, spawnData, spawnTag);
    }
}
