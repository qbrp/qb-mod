package org.qbrp.client.render.text.render.model

import org.qbrp.client.render.text.entity.TextBlock
import org.qbrp.main.engine.text.components.TextComponent

interface TextSplitter {
    fun splitText(text: TextComponent, maxLineWidth: Float): TextBlock
}