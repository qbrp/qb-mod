package mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.qbrp.main.core.Core;
import org.qbrp.main.engine.Engine;
import org.qbrp.main.core.mc.damage.DamageControllerAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class DamageController {

    @Inject(
            method = "damage",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
        DamageControllerAPI api = (DamageControllerAPI) Core.INSTANCE.getModule("damage-controller").getAPI();
        if (api == null) return;

        if (api.isEnabled()) {
            callbackInfo.setReturnValue(false);
            callbackInfo.cancel();
        }

    }
}