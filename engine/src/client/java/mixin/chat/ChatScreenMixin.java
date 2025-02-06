package mixin.chat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.qbrp.engine.client.EngineClient;
import org.qbrp.engine.client.engine.chat.ChatModuleClient;
import org.qbrp.engine.client.engine.chat.system.Typer;
import org.qbrp.engine.client.render.Render;
import org.qbrp.engine.client.render.hud.chat.TransfromTextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(ChatScreen.class)
class ChatScreenMixin extends Screen {

    @Unique
    protected MinecraftClient client = MinecraftClient.getInstance(); // Для непубличных полей

    @Shadow protected TextFieldWidget chatField;
    @Shadow ChatInputSuggestor chatInputSuggestor;
    @Shadow private String originalChatText;
    @Shadow private void onChatFieldUpdate(String chatText) {}

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    public String normalize(String chatText) {
        return StringHelper.truncateChat(StringUtils.normalizeSpace(chatText.trim()));
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onChatOpen(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Objects.requireNonNull(EngineClient.Companion.getChatModuleAPI()).startTyping(player);
        }
        this.chatField = new TransfromTextFieldWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 12, this.width - 4, 12, Text.translatable("chat.editBox")) {
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(chatInputSuggestor.getNarration());
            }
        };
        this.chatField.setMaxLength(256);
        this.chatField.setDrawsBackground(false);
        this.chatField.setText(this.originalChatText);
        this.chatField.setChangedListener(this::onChatFieldUpdate);
        this.chatField.setFocusUnlocked(false);
        this.addSelectableChild(this.chatField);
        this.chatInputSuggestor = new ChatInputSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
        this.chatInputSuggestor.refresh();
        this.setInitialFocus(this.chatField);
        if (this.chatField instanceof TransfromTextFieldWidget) {
            ((TransfromTextFieldWidget) this.chatField).setRenderedTextTransformer(text ->
                    Objects.requireNonNull(EngineClient.Companion.getChatModuleAPI())
                            .getTextTransformer()
                            .getColorTransformedMessage(text)
            );
            ((TransfromTextFieldWidget) this.chatField).setupCustomRenderer();
        }
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onChatClose(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Objects.requireNonNull(EngineClient.Companion.getChatModuleAPI()).endTyping(player);
        }
    }

    @Unique
    public String getTypingMessage() {
        return chatField.getText();
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
        )
    )
    private void redirectFill(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int bgColor = this.client.options.getTextBackgroundColor(Integer.MIN_VALUE);
        Typer.TypingMessageContext typingContext = Objects.requireNonNull(EngineClient.Companion.getChatModuleAPI()).getTypingContextFromText(chatField.getText());
        String tagsNames = typingContext.calculateMetaInfoNames();

        if (getTypingMessage() != null && !tagsNames.isEmpty()) {
            int textWidth = typingContext.calculateMetaInfoWidth(this.client.textRenderer);
            chatField.setX(textWidth + 11);
            context.fill(x1 + textWidth + 6, y1, x2, y2, bgColor);
            return;
        } else {
            chatField.setX(4);
        }
        context.fill(x1, y1, x2, y2, bgColor);
    }

    @ModifyArgs(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"
            )
    )
    private void modifySuperRenderArgs(Args args) {
        int mouseX = args.get(1);
        int mouseY = args.get(2);

        // Модифицируем аргументы
        args.set(1, mouseX + 50); // Изменяем mouseX
        args.set(2, mouseY - 5);  // Изменяем mouseY

    }
    /**
     * @author lain1wakura
     * @reason Перехват полученных сообщений или команд. Легче переписать метод, чем редиректать его
     */
    @Overwrite
    public boolean sendMessage(String chatText, boolean addToHistory) {
        ChatModuleClient.API api = EngineClient.Companion.getChatModuleAPIorThrow();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
           api.endTyping(player);
        }

        chatText = this.normalize(api.getTextTransformer().getColorTransformedMessage(chatText));
        if (chatText.isEmpty()) {
            return true;
        } else {
            if (addToHistory) {
                this.client.inGameHud.getChatHud().addToMessageHistory(chatText);
            }

            if (chatText.startsWith("/")) {
                assert this.client.player != null;
                this.client.player.networkHandler.sendChatCommand(chatText.substring(1));
            } else {
                assert this.client.player != null;
                api.sendMessageToServer(api.createMessageFromContext(api.getTypingContextFromText(chatText)));
            }

            return true;
        }
    }
}