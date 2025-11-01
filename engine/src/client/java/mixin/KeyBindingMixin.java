package mixin;

import net.minecraft.client.option.KeyBinding;
import org.qbrp.client.core.mouse.MouseMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(
            method = "isPressed",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void isPressed$qbrp(CallbackInfoReturnable<Boolean> cir) {
        if (!MouseMixinAccess.INSTANCE.isMouseLocked()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "wasPressed",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void wasPressed$qbrp(CallbackInfoReturnable<Boolean> cir) {
        if (!MouseMixinAccess.INSTANCE.isMouseLocked()) {
            cir.setReturnValue(false);
        }
    }
}
