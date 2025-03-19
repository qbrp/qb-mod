package mixin.chat;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.qbrp.engine.Engine;
import org.qbrp.engine.chat.core.messages.ChatMessage;
import org.qbrp.engine.client.engine.chat.ChatModuleClient;
import org.qbrp.engine.client.engine.chat.ClientChatAPI;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    // Поле для хранения api, изначально null
    private ClientChatAPI api;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        // Ничего не делаем здесь, оставляем visibleMessages как есть (инициализируется пустым списком в ChatHud)
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        // Инициализируем api, если он еще не установлен
        if (api == null) {
            api = ((ChatModuleClient) Objects.requireNonNull(Engine.Companion.getModuleManager().getModule("chat"))).getAPI();
        }
        // Обновляем visibleMessages из api
        this.visibleMessages = api.getMessageProvider().provide(api.getStorage());
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        if (api != null) {
            api.addMessage(ChatMessage.Companion.create(message, SYSTEM_MESSAGE_AUTHOR)); // Предполагается, что api имеет метод addMessage
            ci.cancel(); // Отменяем оригинальный метод, так как api теперь управляет сообщениями
        }
        // Если api еще null, даем оригинальному методу обработать сообщение
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void onClear(CallbackInfo ci) {
        if (api != null) {
            api.clearStorage(); // Очищаем хранилище api
            ci.cancel(); // Отменяем оригинальный метод
        }
        // Если api еще null, оригинальный метод очистит visibleMessages
    }
}