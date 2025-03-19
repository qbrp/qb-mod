package org.qbrp.engine.music.plasmo

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text

open class ClickableButton(
    protected val label: String,
    protected val color: Int,
    protected val command: String,
    protected val hoverText: String,
    protected val action: ClickEvent.Action = ClickEvent.Action.SUGGEST_COMMAND,
) {
    open fun toText(): Text {
        return Text.literal(label)
            .setStyle(
                Style.EMPTY
                    .withColor(color)
                    .withClickEvent(ClickEvent(action, command))
                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hoverText)))
            )
    }
}
