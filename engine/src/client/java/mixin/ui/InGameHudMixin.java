package mixin.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.qbrp.client.render.ActionBarMixinRenderData;
import org.qbrp.client.render.ui.interaction.crosshair.CrosshairMixinRenderData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void qbrp$disableHotBar(float tickDelta, DrawContext context, CallbackInfo ci) {
        if (!ActionBarMixinRenderData.INSTANCE.getRENDER_ACTION_BAR()) ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void qbrp$disableExpBar(DrawContext context, int x, CallbackInfo ci) {
        if (!ActionBarMixinRenderData.INSTANCE.getRENDER_SURVIVAL_BAR()) ci.cancel();
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void qbrp$disableHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        if (!ActionBarMixinRenderData.INSTANCE.getRENDER_SURVIVAL_BAR()) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void qbrp$disableCrosshair(DrawContext context, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void qbrp$disableStatusBars(DrawContext context, CallbackInfo ci) {
        if (!ActionBarMixinRenderData.INSTANCE.getRENDER_SURVIVAL_BAR()) ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void qbrp$disableItemTooltip(DrawContext context, CallbackInfo ci) {
        if (!ActionBarMixinRenderData.INSTANCE.getRENDER_ACTION_BAR()) ci.cancel();
    }
}
