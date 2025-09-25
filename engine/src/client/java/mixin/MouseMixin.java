package mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.qbrp.client.core.mouse.MouseMixinAccess;
import org.qbrp.client.render.Render;
import org.qbrp.client.core.mouse.MouseModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton(JIII)V", at = @At("TAIL"), cancellable = true)
    private void qbrp$afterMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        float mx = (float) client.mouse.getX();
        float my = (float) client.mouse.getY();

        if (action == GLFW.GLFW_PRESS) {
            boolean shouldSkip = MouseMixinAccess.INSTANCE.emitMouseClickEvent(mx, my, button);
            if (shouldSkip) {
                ci.cancel();
            }
        }
        else if (action == GLFW.GLFW_RELEASE) {
            boolean handled = MouseMixinAccess.INSTANCE.emitMouseReleasedEvent(mx, my, button);
            if (handled) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;lockCursor()V"))
    private void onMouseButton$cancelLockCursor(Mouse instance) {}

    @Inject(method = "onCursorPos(JDD)V", at = @At("TAIL"))
    private void qbrp$afterCursorPos(long window, double x, double y, CallbackInfo ci) {
        int state = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        if (state == GLFW.GLFW_PRESS) {
            MouseMixinAccess.INSTANCE.emitMouseDraggedEvent((float) x, (float) y);
        }
    }
}
