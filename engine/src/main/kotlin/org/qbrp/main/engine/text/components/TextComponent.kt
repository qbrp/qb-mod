package org.qbrp.main.engine.text.components

import org.qbrp.main.core.mc.text.MinecraftText
import org.qbrp.main.engine.players.model.context.PlayerContext
import org.qbrp.main.engine.text.Transformer
import org.qbrp.main.engine.text.style.Style

interface TextComponent: Transformer<TextComponent, PlayerContext>, MinecraftText {
    val style: Style get() = Style.Companion.DEFAULT
    override fun transform(ctx: PlayerContext): TextComponent {
        return this
    }
}