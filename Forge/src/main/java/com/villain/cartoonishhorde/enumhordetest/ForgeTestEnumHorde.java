package com.villain.cartoonishhorde.enumhordetest;

import com.villain.cartoonishhorde.hordes.EntityEnumHorde;
import com.villain.cartoonishhorde.RuleEnumInterface;
import com.villain.cartoonishhorde.hordedata.EnumHordeData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.*;

import java.util.Optional;

public class ForgeTestEnumHorde extends EntityEnumHorde {
    public ForgeTestEnumHorde(MinecraftServer server) {
        super(server);
    }

    @Override
    protected void spawnBasedOnEnum(RuleEnumInterface enumSelected, EnumHordeData entrySelected) {
        Optional<BlockPos> hordeSpawn = Optional.empty();

        PathfinderMob mobToSpawn = null;
        if (enumSelected instanceof ForgeTestHordeDataClass) {
            if (enumSelected.equals(ForgeTestHordeDataClass.CREEPEROVERZOMBIENETHER)) {
                if (world.dimension().toString().contains("nether")) {
                    mobToSpawn = new Zombie(EntityType.ZOMBIE, world);
                } else {
                    mobToSpawn = new Creeper(EntityType.CREEPER, world);
                }
            } else if (enumSelected.equals(ForgeTestHordeDataClass.SPIDEROVEREVOKERNETHER)) {
                if (world.dimension().toString().contains("nether")) {
                    mobToSpawn = new Evoker(EntityType.EVOKER, world);
                } else {
                    mobToSpawn = new Spider(EntityType.SPIDER, world);

                }
            } else if (enumSelected.equals(ForgeTestHordeDataClass.VINDICATOROVERSKELETONNETHER)) {
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
                hordeSpawn = this.getValidSpawn(2, mobToSpawn.getType());
                attempts++;
                if (hordeSpawn.isEmpty() && attempts >= 5) {
                    this.Stop(EntityEnumHorde.HordeStopReasons.SPAWN_ERROR);
                    return;
                }
            }

            mobToSpawn.setPos(hordeSpawn.get().getX(), hordeSpawn.get().getY(), hordeSpawn.get().getZ());
            injectGoal(mobToSpawn, entrySelected, entrySelected.getGoalMovementSpeed());
            world.addFreshEntity(mobToSpawn);
            SpawnUnit();
            activeHordeMembers.add(mobToSpawn);
        }
    }
}
