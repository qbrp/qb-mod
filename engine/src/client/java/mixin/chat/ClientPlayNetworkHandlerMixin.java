package mixin.chat;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.qbrp.engine.Engine;
import org.qbrp.engine.chat.core.messages.ChatMessage;
import org.qbrp.engine.chat.core.messages.VanillaChatMessage;
import org.qbrp.engine.client.EngineClient;
import org.qbrp.engine.client.engine.chat.ChatModuleClient;
import org.qbrp.engine.client.engine.chat.ClientChatAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

import static org.qbrp.engine.chat.ChatModule.SYSTEM_MESSAGE_AUTHOR;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Redirect(method = "onGameMessage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"))
    private void redirectOnGameMessage(net.minecraft.client.network.message.MessageHandler instance, Text message, boolean overlay) {
        //if (EngineClient.Companion.getChatClientModule().getEnabled()) {
            if (!overlay) {
                ChatMessage chatMessage = VanillaChatMessage.Companion.create(message, SYSTEM_MESSAGE_AUTHOR);
                Objects.requireNonNull((ChatModuleClient) Engine.Companion.getModuleManager().getModule("chat")).getAPI().addMessage(chatMessage);
            } else {
                instance.onGameMessage(message, overlay);
            }
//        } else {
//            instance.onGameMessage(message, overlay);
//        }
    }
}
