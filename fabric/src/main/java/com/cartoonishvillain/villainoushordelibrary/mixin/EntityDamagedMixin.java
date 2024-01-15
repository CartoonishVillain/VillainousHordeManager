package com.cartoonishvillain.villainoushordelibrary.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary.isHordeMember;

@Mixin(LivingEntity.class)
public class EntityDamagedMixin {

    @Inject(at = @At("INVOKE"), method = "actuallyHurt")
    private void fabricHordeLibraryActuallyHurt(DamageSource damageSource, float f, CallbackInfo info) {
        LivingEntity entity = ((LivingEntity)(Object)this);
        if(entity instanceof PathfinderMob && isHordeMember((PathfinderMob) entity) && entity.tickCount < 2) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
