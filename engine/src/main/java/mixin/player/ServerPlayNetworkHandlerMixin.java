package mixin.player;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.qbrp.main.engine.Engine;
import org.qbrp.main.engine.chat.addons.SystemMessages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Unique
    private ServerPlayerEntity currentPlayer;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void capturePlayer(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.currentPlayer = player;
    }

    @Inject(
            method = "onDisconnected",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private void sendDisconnectedMessage(Text reason, CallbackInfo ci) {
        Object module = Engine.INSTANCE.getModule("chat-addon-system-messages");
        if (module instanceof SystemMessages) {
            ((SystemMessages) module).sendLeaveMessage(currentPlayer);
        }
    }

    @Inject(
            method = "onPlayerMove",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void disablePlayerMovePacketHandler(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V")
    )
    private void disablePlayerMovementTick(ServerPlayerEntity instance, double v1, double v2, double v3, float v4, float v5) {}
}
