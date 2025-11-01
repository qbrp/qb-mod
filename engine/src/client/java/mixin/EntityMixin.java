package mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.qbrp.client.engine.players.LocalPlayerMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(
            method = "getVelocity",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getVelocity$qbrp(CallbackInfoReturnable<Vec3d> cir) {
        if ((Object)this instanceof ClientPlayerEntity) {
            Vec3d v = LocalPlayerMixinAccess.INSTANCE.getLocalPlayerVelocity();
            if (v != null) {
                cir.setReturnValue(v);
            }
        }
    }

    @Inject(
            method = "isOnGround",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isOnGround$qbrp(CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof ClientPlayerEntity) {
            Boolean isOnGround = LocalPlayerMixinAccess.INSTANCE.isLocalPlayerOnGround();
            if (isOnGround != null) {
                cir.setReturnValue(isOnGround);
            }
        }
    }
}
