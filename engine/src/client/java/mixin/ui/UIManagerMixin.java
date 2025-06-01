package mixin.ui;

import icyllis.modernui.annotation.MainThread;
import icyllis.modernui.fragment.FragmentContainerView;
import icyllis.modernui.fragment.FragmentController;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.mc.MuiScreen;
import icyllis.modernui.mc.TooltipRenderer;
import icyllis.modernui.mc.UIManager;
import icyllis.modernui.view.ViewRoot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.apache.logging.log4j.Marker;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

import static icyllis.modernui.ModernUI.LOGGER;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;

@Mixin(value = UIManager.class, remap = false)
public class UIManagerMixin {
    @Shadow
    protected volatile MuiScreen mScreen;

    @Final
    @Mutable
    @Shadow
    protected static Marker MARKER;

    @Shadow
    protected volatile FragmentController mFragmentController;

    @Final
    @Mutable
    @Shadow
    protected static int fragment_container;

    @Final
    @Mutable
    @Shadow
    protected Window mWindow;

    @Shadow
    private FragmentContainerView mFragmentContainerView;

    /**
     * @author Quarri6343, adapted by Lain1wakura
     * @reason for regsitering HUD without setting it as current screen
     */
    @Overwrite
    public void initScreen(@Nonnull MuiScreen screen) {
        ViewRoot mRoot = null;
        try {
            Field mRootField = UIManager.class.getDeclaredField("mRoot");
            mRootField.setAccessible(true);
            mRoot = (ViewRoot) mRootField.get(UIManager.getInstance());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (mScreen != screen) {
            if (mScreen != null) {
                LOGGER.warn(MARKER, "You cannot set multiple screens.");
                removed(null);
            }
            mRoot.mHandler.post(this::suppressLayoutTransition);
            mFragmentController.getFragmentManager().beginTransaction()
                    .add(fragment_container, screen.getFragment(), "main")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            mRoot.mHandler.post(this::restoreLayoutTransition);
        }
        //if (!(screen.getFragment() instanceof GameScreen)) {
            //mScreen = screen;
        //}
        // ensure it's resized
        MinecraftClient minecraft = MinecraftClient.getInstance();
        resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
    }


    /**
     * @author Quarri6343, adapted by lain1wakura
     * @reason prevent HUD being removed when the current screen removal by
     */
    @MainThread
    @Overwrite
    public void removed(@Nullable Screen target) {
        ViewRoot mRoot;
        try {
            Field mRootField = UIManager.class.getDeclaredField("mRoot");
            mRootField.setAccessible(true);
            mRoot = (ViewRoot) mRootField.get(UIManager.getInstance());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        MuiScreen screen = mScreen;
        if (screen == null) {
            return;
        }
        mRoot.mHandler.post(this::suppressLayoutTransition);
        mFragmentController.getFragmentManager().beginTransaction()
                .remove(screen.getFragment())
//                .runOnCommit(() -> mFragmentContainerView.removeAllViews())
                .runOnCommit(() -> mFragmentContainerView.removeView(screen.getFragment().getView()))
                .commit();
        mRoot.mHandler.post(this::restoreLayoutTransition);
        mScreen = null;
        glfwSetCursor(this.mWindow.getHandle(), MemoryUtil.NULL);
    }

    @Shadow
    void resize(int width, int height) {
    }

    @Shadow
    void suppressLayoutTransition() {
    }

    @Shadow
    void restoreLayoutTransition() {
    }

    @Redirect(method = "onRenderTick", at = @At(value = "INVOKE", target = "Licyllis/modernui/mc/TooltipRenderer;update(JJ)V"))
    protected void onRenderTick(TooltipRenderer tooltipRenderer, long deltaMillis, long timeMillis) {
        tooltipRenderer.update(deltaMillis, timeMillis);
    }
}