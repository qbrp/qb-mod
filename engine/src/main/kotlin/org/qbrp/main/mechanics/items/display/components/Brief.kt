package org.qbrp.main.mechanics.items.display.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.LiteralTextComponent
import org.qbrp.main.engine.text.components.MiniMessageTextComponent
import org.qbrp.main.mechanics.items.display.PlainTextTooltip

@Serializable
/** Краткое описание предмета, отображается для всех игроков одинаково. **/
class Brief(val text: String): DataComponent, PlainTextTooltip {
    override fun provide(
        player: ServerPlayerObject,
        item: ServerItemObject,
    ): TextComponent {
        return MiniMessageTextComponent(text)
    }
}