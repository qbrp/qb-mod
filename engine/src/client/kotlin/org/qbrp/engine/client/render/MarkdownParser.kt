package org.qbrp.engine.client.render

import org.commonmark.node.Node
import org.commonmark.parser.Parser

object MarkdownParser {
    fun getMarkdownText(text: String): Node {
        val parser: Parser = Parser.builder().build()
        val document: Node = parser.parse(text)
        return document
    }
}