package com.cartoonishvillain.villainoushordelibrary.codebasedhordetest;

import com.cartoonishvillain.villainoushordelibrary.RuleEnumInterface;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EnumHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityEnumHorde;
import com.cartoonishvillain.villainoushordelibrary.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.*;

import java.util.Optional;

public class TestEnumHorde extends EntityEnumHorde {
    public TestEnumHorde(MinecraftServer server) {
        super(server);
    }

    @Override
    protected void spawnBasedOnEnum(RuleEnumInterface enumSelected, EnumHordeData entrySelected) {
        Optional<BlockPos> hordeSpawn = Optional.empty();

        PathfinderMob mobToSpawn = null;
        if (enumSelected instanceof TestHordeDataClass) {
            if (enumSelected.equals(TestHordeDataClass.CREEPEROVERZOMBIENETHER)) {
                if (world.dimension().toString().contains("nether")) {
                    mobToSpawn = new Zombie(EntityType.ZOMBIE, world);
                } else {
                    mobToSpawn = new Creeper(EntityType.CREEPER, world);
                }
            } else if (enumSelected.equals(TestHordeDataClass.SPIDEROVEREVOKERNETHER)) {
                if (world.dimension().toString().contains("nether")) {
                    mobToSpawn = new Evoker(EntityType.EVOKER, world);
                } else {
                    mobToSpawn = new Spider(EntityType.SPIDER, world);

                }
            } else if (enumSelected.equals(TestHordeDataClass.VINDICATOROVERSKELETONNETHER)) {
                if (world.dimension().toString().contains("nether")) {
                    mobToSpawn = new Skeleton(EntityType.SKELETON, world);
                } else {
                    mobToSpawn = new Vindicator(EntityType.VINDICATOR, world);
                }
            }
        }

        if (mobToSpawn != null) {
            int attempts = 0;
            while (hordeSpawn.isEmpty()) {
                hordeSpawn = this.getValidSpawn(10, mobToSpawn.getType());
                attempts++;
                if (hordeSpawn.isEmpty() && attempts >= 5) {
                    this.Stop(HordeStopReasons.SPAWN_ERROR);
                    return;
                }
            }

            mobToSpawn.setPos(hordeSpawn.get().getX(), hordeSpawn.get().getY(), hordeSpawn.get().getZ());
            injectGoal(mobToSpawn, entrySelected, entrySelected.getGoalMovementSpeed());
            Services.PLATFORM.finalizeSpawn(mobToSpawn, world, mobToSpawn.level().getCurrentDifficultyAt(mobToSpawn.getOnPos()), MobSpawnType.EVENT, null, null);
            world.addFreshEntity(mobToSpawn);
            SpawnUnit();
            activeHordeMembers.add(mobToSpawn);
        }
    }
}
