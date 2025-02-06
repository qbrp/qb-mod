package org.qbrp.engine.music.plasmo.controller.view

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.qbrp.engine.music.plasmo.model.audio.Playable

class MusicClickableButton(
    label: String,
    color: Int,
    command: String,
    hoverText: String,
    action: ClickEvent.Action = ClickEvent.Action.SUGGEST_COMMAND,
    val playlist: Playable
): ClickableButton(label, color, command, hoverText, action) {
    override fun toText(): Text {
        return Text.literal(label)
            .setStyle(
                Style.EMPTY
                    .withColor(color)
                    .withClickEvent(ClickEvent(action, command))
                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hoverText)))
            )
    }
}
