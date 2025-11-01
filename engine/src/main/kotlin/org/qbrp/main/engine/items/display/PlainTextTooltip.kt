package org.qbrp.main.engine.items.display

import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.text.components.TextComponent

interface PlainTextTooltip {
    fun provide(player: ServerPlayerObject, item: ServerItemObject): TextComponent
}