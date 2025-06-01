package mixin.chat;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.qbrp.client.engine.ClientEngine;
import org.qbrp.client.ClientCore;
import org.qbrp.client.engine.chat.ClientChatAPI;
import org.qbrp.client.engine.chat.ChatModuleClient;
import org.qbrp.main.engine.chat.core.messages.ChatMessage;
import org.qbrp.main.engine.chat.core.messages.VanillaChatMessage;

import static org.qbrp.main.engine.chat.ChatModule.SYSTEM_MESSAGE_AUTHOR;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Redirect(
            method = "onGameMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private void redirectOnGameMessage(
            MessageHandler handler,
            Text message,
            boolean overlay
    ) {
        if (overlay || !ClientEngine.INSTANCE.isModuleEnabled("chat-client")) {
            handler.onGameMessage(message, overlay);
            return;
        }

        ChatModuleClient chatModule = ClientCore.INSTANCE.getModule("chat");
        if (chatModule == null) {
            handler.onGameMessage(message, overlay);
            return;
        }
        ClientChatAPI api = (ClientChatAPI) chatModule.getAPI();
        if (api == null) {
            handler.onGameMessage(message, overlay);
            return;
        }

        // Create and add custom chat message
        ChatMessage chatMessage = VanillaChatMessage.Companion.create(message, SYSTEM_MESSAGE_AUTHOR);
        api.addMessage(chatMessage);
    }
}
