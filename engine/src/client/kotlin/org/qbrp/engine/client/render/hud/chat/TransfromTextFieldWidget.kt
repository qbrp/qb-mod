package org.qbrp.engine.client.render.hud.chat

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import org.qbrp.system.utils.format.Format

open class TransfromTextFieldWidget(
    textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    text: Text
) : TextFieldWidget(textRenderer, x, y, width, height, text) {

    private var textTransformer: (String) -> String = { it }

    fun setupCustomRenderer() {
        this.setRenderTextProvider { text, firstCharIndex ->
            val transformed = textTransformer(text)
            // Создаем стиль с нужным цветом (например, красный)
            Format.formatOrderedText(transformed)
        }
    }

    fun setRenderedTextTransformer(transformer: (String) -> String) {
        textTransformer = transformer
    }

    fun getRenderedText(): String {
        return textTransformer(super.getText())
    }

}
