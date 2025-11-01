package org.qbrp.client.render.text.render.impl

import net.minecraft.client.font.TextRenderer
import org.qbrp.client.render.text.entity.McOrderedText
import org.qbrp.client.render.text.entity.TextBlock
import org.qbrp.client.render.text.render.model.TextSplitter
import org.qbrp.main.core.fabricmc.text.MinecraftTextMapper
import org.qbrp.main.engine.text.components.TextComponent

class TextSplitterImpl(private val mcTextRender: TextRenderer): TextSplitter {
    override fun splitText(
        text: TextComponent,
        maxLineWidth: Float
    ): TextBlock {
        val mcText = MinecraftTextMapper.fromEngineText(text)
        val lines = mcTextRender.wrapLines(mcText, maxLineWidth.toInt())
        return TextBlock(lines.map { McOrderedText(it) })
    }
}