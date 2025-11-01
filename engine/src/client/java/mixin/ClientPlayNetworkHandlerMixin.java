package mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Redirect(
            method = "onServerMetadata",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/toast/ToastManager;add(Lnet/minecraft/client/toast/Toast;)V"
            )
    )
    private void redirectOnGameMessage(
            ToastManager instance, Toast toast
    ) {}

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"), cancellable = true)
    private void disablePositionLookPacket(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        ci.cancel();
    }
}
