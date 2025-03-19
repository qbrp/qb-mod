package org.qbrp.engine.client.render.screen.chat

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

open class TransfromTextFieldWidget(
    textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    text: Text
) : TextFieldWidget(textRenderer, x, y, width, height, text) {

    private var textTransformer: (String) -> String = { it }

    fun setRenderedTextTransformer(transformer: (String) -> String) {
        textTransformer = transformer
    }

    fun getRenderedText(): String {
        return textTransformer(super.getText())
    }

}
