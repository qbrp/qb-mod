package org.qbrp.client.render.utils

import icyllis.modernui.text.SpannableString
import icyllis.modernui.text.Spanned
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer

object MarkdownParser {
    fun getMarkdownText(text: String): Spanned {
        val parser: Parser = Parser.builder().build()
        val document: Node = parser.parse(text)
        val renderer: MarkdownRenderer = MarkdownRenderer.builder().build()
        val renderedText: String = renderer.render(document)
        return SpannableString(renderedText)
    }
}