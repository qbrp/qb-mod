package org.qbrp.main.engine.anticheat

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.sendMessage
import org.qbrp.main.core.mc.text.miniMessage

class UndesirableMod(override val id: String, val warn: String): Mod {
    context(mc: MinecraftContext)
    override fun ifFounded(
        player: PlayerSnapshot,
        notifier: OpNotificator
    ) {
        player.sendMessage(miniMessage(warn))
    }
}
