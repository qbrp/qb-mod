package org.qbrp.main.core.fabricmc.text

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextColor as McTextColor
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.text.components.LiteralTextComponent
import org.qbrp.main.engine.text.components.MiniMessageTextComponent
import org.qbrp.main.engine.text.components.RawTextComponent
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.TextSequence

object MinecraftTextMapper {
    fun map(text: MinecraftText): Text {
        return when (text) {
            is EmptyText -> Text.empty()
            is MiniMessageText -> fromMiniMessage(text)
            is TextComponent -> fromEngineText(text)
            else -> throw NotImplementedError()
        }
    }

    fun fromMiniMessage(text: MiniMessageText): Text {
        return text.value.formatMm()
    }

    fun fromEngineText(component: TextComponent): Text {
        return when (component) {
            is MiniMessageTextComponent ->
                component.data.formatMm()
            is LiteralTextComponent ->
                Text.literal(component.text).applyEngineStyle(component.style)
            is TextSequence ->
                component.children.fold(Text.empty()) { acc, child -> acc.append(fromEngineText(child)) }
            is RawTextComponent ->
                component.mcText
            else -> throw NotImplementedError()
        }
    }

    private fun MutableText.applyEngineStyle(eStyle: Style): MutableText {
        setStyle(style.withColor(McTextColor.fromRgb(eStyle.color.hex)))
        return this
    }
}