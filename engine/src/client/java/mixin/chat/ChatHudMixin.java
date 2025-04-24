package mixin.chat;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.qbrp.engine.Engine;
import org.qbrp.engine.chat.core.messages.ChatMessage;
import org.qbrp.engine.chat.core.messages.VanillaChatMessage;
import org.qbrp.engine.client.EngineClient;
import org.qbrp.engine.client.engine.chat.ChatModuleClient;
import org.qbrp.engine.client.engine.chat.ClientChatAPI;
import org.qbrp.engine.client.engine.chat.system.HandledMessage;
import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static org.qbrp.engine.chat.ChatModule.SYSTEM_MESSAGE_AUTHOR;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Mutable
    @Final
    @Shadow
    private List<ChatHudLine.Visible> visibleMessages;

    @Shadow private boolean hasUnreadNewMessages;

    @Shadow public abstract void scroll(int scroll);

    @Shadow private int scrolledLines;

    @Shadow protected abstract boolean isChatFocused();

    // Поле для хранения api, изначально null
    @Unique
    private ClientChatAPI api;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        MessageAddedEvent.Companion.getEVENT().register((message, storage) -> {
            if (isChatFocused() && scrolledLines > 0) {
                int size = HandledMessage.Companion.from(message).getText().size();
                scroll(0);
            }
            return ActionResult.SUCCESS;
        });
        // Ничего не делаем здесь, оставляем visibleMessages как есть (инициализируется пустым списком в ChatHud)
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        // Инициализируем api, если он еще не установлен
        if (api == null) {
            api = ((ChatModuleClient) Objects.requireNonNull(EngineClient.Companion.getModuleManager().getModule("chat-client"))).getAPI();
        } else {
            // Обновляем visibleMessages из api
            this.visibleMessages = api.getMessageProvider().provide(api.getStorage());
        }
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        if (api != null) {
            ChatMessage msg = VanillaChatMessage.Companion.create(message, SYSTEM_MESSAGE_AUTHOR);
            api.addMessage(msg);
            //ci.cancel();
        }
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void onClear(CallbackInfo ci) {
        if (api != null) {
            api.clearStorage(); // Очищаем хранилище api
            ci.cancel(); // Отменяем оригинальный метод
        }
    }

}