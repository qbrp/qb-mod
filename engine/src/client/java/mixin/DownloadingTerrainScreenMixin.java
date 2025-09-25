package mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.text.Text;
import org.qbrp.client.core.networking.preparation.SyncPreparationMixinAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public abstract class DownloadingTerrainScreenMixin {

    @Shadow private boolean closeOnNextTick;
    @Unique private boolean worldLoaded = false;

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
            ),
            index = 1
    )
    private Text syncprep_modifyDisplayedText(Text text) {
        if (!worldLoaded) {
            return text;
        } else {
            return Text.of("[ENGINE] Подготовка мира...");
        }
    }

    @Inject(method = "setReady", at = @At("TAIL"))
    private void syncprep_onReady(CallbackInfo ci) {
        SyncPreparationMixinAccess.INSTANCE.onReady();
        worldLoaded = true;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void syncprep_tick(CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isInSingleplayer()) {
            this.closeOnNextTick = SyncPreparationMixinAccess.INSTANCE.isReady();
        }
    }
}