package com.cartoonishvillain.villainoushordelibrary.platform;

import com.cartoonishvillain.villainoushordelibrary.ForgeVillainousHordeLibrary;
import com.cartoonishvillain.villainoushordelibrary.platform.services.IPlatformHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
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
        return ForgeVillainousHordeLibrary.LOGGER;
    }

    @Override
    public void finalizeSpawn(PathfinderMob mobToSpawn, ServerLevel world, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag) {
        ForgeEventFactory.onFinalizeSpawn(mobToSpawn, world, difficultyInstance, mobSpawnType, spawnData, spawnTag);
    }
}