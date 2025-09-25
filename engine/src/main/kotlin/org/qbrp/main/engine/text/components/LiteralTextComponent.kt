package org.qbrp.main.engine.text.components

import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.components.TextComponent

data class LiteralTextComponent(override val text: String, override val style: Style = Style.Companion.DEFAULT): TextComponent, TextContent