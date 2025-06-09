package mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.qbrp.client.render.Render;
import org.qbrp.client.render.hud.HudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton(JIII)V", at = @At("TAIL"), cancellable = true)
    private void qbrp$afterMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        double mx = client.mouse.getX();
        double my = client.mouse.getY();

        HudRenderer hudModule = Render.INSTANCE.getModule("hud");
        if (hudModule == null) return;

        if (action == GLFW.GLFW_PRESS) {
            boolean handled = hudModule.mouseClicked(mx, my, button);
            if (handled) {
                ci.cancel();
            }
        }
        else if (action == GLFW.GLFW_RELEASE) {
            boolean handled = hudModule.mouseReleased(mx, my, button);
            if (handled) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onCursorPos(JDD)V", at = @At("TAIL"))
    private void qbrp$afterCursorPos(long window, double x, double y, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        HudRenderer hud = Render.INSTANCE.getModule("hud");
        if (hud == null) return;

        // Проверяем, что левая кнопка мыши сейчас удерживается
        int state = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        if (state == GLFW.GLFW_PRESS) {
            // Вызываем drag-логику
            hud.mouseDragged(x, y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        }
        // То же можно сделать и для правой, если нужно
    }
}
