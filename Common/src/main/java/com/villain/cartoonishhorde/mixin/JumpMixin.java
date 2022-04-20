package com.villain.cartoonishhorde.mixin;

import com.villain.cartoonishhorde.CommonCartoonishHorde;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class JumpMixin {
    @Inject(at = @At("TAIL"), method = "jumpFromGround")
    private void jumpFromGround(CallbackInfo ci) {
        Player player = ((Player) (Object) this);
        if(player instanceof ServerPlayer && !CommonCartoonishHorde.horde.getHordeActive()) {
            //Step 4 - Start.
            CommonCartoonishHorde.jumpBoi((ServerPlayer) player);
        }
    }
}
