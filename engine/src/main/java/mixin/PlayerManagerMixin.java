package mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.qbrp.engine.Engine;
import org.qbrp.engine.chat.addons.SystemMessages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Unique
    private ServerPlayerEntity currentPlayer;

    // Захватываем игрока при подключении
    @Inject(
            method = "onPlayerConnect",
            at = @At("HEAD")
    )
    private void capturePlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        this.currentPlayer = player;
    }

    // Перехватываем сообщение о входе игрока
    @Inject(
            method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private void redirectBroadcastOnJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        Object module = Engine.Companion.getModuleManager().getModule("chat-addon-system-messages");
        if (module instanceof SystemMessages) {
            ((SystemMessages) module).sendJoinMessage(player);
        }
    }
}