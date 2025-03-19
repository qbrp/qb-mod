package org.qbrp.engine.music.plasmo.view

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.qbrp.engine.music.plasmo.ClickableButton

class LinkClickableButton(
    link: String,
    color: Int,
): ClickableButton(link, color, link,"Открыть ссылку $link", ClickEvent.Action.OPEN_URL) {
    override fun toText(): Text {
        return Text.literal(label)
            .setStyle(
                Style.EMPTY
                    .withColor(color)
                    .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, command))
                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hoverText)))
                    .withUnderline(true)
            )
    }
}
