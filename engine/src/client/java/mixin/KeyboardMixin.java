package mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.qbrp.client.core.keybinds.KeyboardMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(
            method = "onKey",
            at = @At(
                    value = "HEAD",
                    target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
            ),
            cancellable = true)
    private void beforeScreenGet(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (KeyboardMixinAccess.INSTANCE.emitKeyEvent(key, action)) { ci.cancel(); };
        if (action == GLFW.GLFW_PRESS) {
            if (KeyboardMixinAccess.INSTANCE.emitKeyPressEvent(key)) { ci.cancel(); };
        } else if (action == GLFW.GLFW_RELEASE) {
            if (KeyboardMixinAccess.INSTANCE.emitKeyReleaseEvent(key)) { ci.cancel(); };
        } else if (action == GLFW.GLFW_REPEAT) {
            if (KeyboardMixinAccess.INSTANCE.emitKeyRepeatEvent(key)) { ci.cancel(); };
        }
    }

    @Redirect(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;openPauseMenu(Z)V"
            )
    )
    private void onOpenPauseMenu(MinecraftClient instance, boolean pause) {}
}
