package mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.qbrp.engine.Engine;
import org.qbrp.engine.chat.addons.SystemMessages;
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
        Object module = Engine.Companion.getModuleManager().getModule("chat-addon-system-messages");
        if (module instanceof SystemMessages) {
            ((SystemMessages) module).sendLeaveMessage(currentPlayer);
        }
    }

}
