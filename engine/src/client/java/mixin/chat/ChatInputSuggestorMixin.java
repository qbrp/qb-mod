package mixin.chat;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.qbrp.engine.client.EngineClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Final @Shadow MinecraftClient client;
    @Final @Shadow TextFieldWidget textField;
    @Shadow @javax.annotation.Nullable private CompletableFuture<Suggestions> pendingSuggestions;
    @Final @Shadow TextRenderer textRenderer;

    @Unique
    private String cachedOriginalText = "";
    @Unique
    private String cachedRenderedText = "";

    @Redirect(
            method = "show",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getCharacterX(I)I"
            )
    )
    private int redirectGetCharacterX(TextFieldWidget widget, int index) {
        String currentText = widget.getText();
        if (!currentText.equals(cachedOriginalText)) {
            cachedRenderedText = EngineClient.Companion.getChatModuleAPI()
                    .getTextTransformer().getColorTransformedMessage(currentText);
            cachedOriginalText = currentText;
        }
        int safeIndex = Math.min(index, cachedRenderedText.length());
        return textRenderer.getWidth(cachedRenderedText.substring(0, safeIndex));
    }

    // Перехватываем метод refresh() в самом начале
    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void onRefresh(CallbackInfo ci) {
        String text = textField.getText();
        int cursor = textField.getCursor();
        int atIndex = text.lastIndexOf("@");

        // Если '@' найден и курсор стоит после него, обрабатываем как подсказки ников
        if (atIndex != -1 && cursor > atIndex) {
            // Вычисляем подстроку, которая идет после символа '@'
            String lowerPartial = text.substring(atIndex + 1, cursor).toLowerCase();

            // Получаем список ников игроков (адаптируйте под свой способ получения списка)
            assert client.player != null;
            Collection<String> playerNames = client.player.networkHandler.getPlayerList().stream()
                    .map(player -> player.getProfile().getName())
                    .collect(Collectors.toList());

            // Создаем билдер подсказок, передавая полный текст ввода и позицию, с которой начинаются предложения (atIndex + 1)
            SuggestionsBuilder builder = new SuggestionsBuilder(text, atIndex + 1);

            // Добавляем в подсказки все ники, которые начинаются с введенной части (без учета регистра)
            for (String nick : playerNames) {
                if (nick.toLowerCase().startsWith(lowerPartial)) {
                    builder.suggest(nick);
                }
            }

            // Завершаем CompletableFuture с подсказками
            this.pendingSuggestions = CompletableFuture.completedFuture(builder.build());

            // Вызываем метод, который отображает окно подсказок (в оригинале уже реализован)
            ((ChatInputSuggestor)(Object)this).show(false);

            // Прерываем выполнение оригинального refresh()
            ci.cancel();
        }
    }
}
