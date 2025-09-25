package org.qbrp.main.core.mc.text

import net.minecraft.text.Text

fun emptyMcText(): MinecraftText {
    return EmptyText()
}

fun miniMessage(text: String): MiniMessageText {
    return MiniMessageText(text)
}

fun MinecraftText.map(): Text {
    return MinecraftTextMapper.map(this)
}