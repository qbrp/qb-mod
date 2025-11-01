package org.qbrp.main.engine.text.components

import org.qbrp.main.core.fabricmc.text.MinecraftTextMapper
import org.qbrp.main.core.fabricmc.text.miniMessage

data class MiniMessageTextComponent(val data: String): TextComponent, TextContent {
    override val text: String get() = MinecraftTextMapper.fromMiniMessage(miniMessage(data)).string
}