package com.cartoonishvillain.cartoonishhorde;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

//based on move towards raid goal
public class HordeMovementGoal<T extends PathfinderMob> extends Goal {
    private final T Member;
    private final Horde hordeInstance;
    private final double movementModifier;

    public HordeMovementGoal(T member, Horde hordeInstance, Double movementModifier){
        this.Member = member;
        this.hordeInstance = hordeInstance;
        this.movementModifier = movementModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.hordeInstance.getHordeActive() && this.hordeInstance.getCenter() != null && hordeInstance.isHordeMember(Member) && this.Member.getTarget() == null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.Member.isPathFinding()) {
            Vec3 vector3d = DefaultRandomPos.getPosTowards(this.Member, 15, 4, Vec3.atBottomCenterOf(hordeInstance.getCenter()), ((float)Math.PI / 10F));
            if (vector3d != null) {
                this.Member.getNavigation().moveTo(vector3d.x, vector3d.y, vector3d.z, movementModifier);
            }
        }
    }
}
