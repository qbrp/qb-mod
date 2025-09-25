package org.qbrp.main.engine.anticheat

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.disconnect
import org.qbrp.main.engine.players.model.networking.DisconnectReason

class RestrictedMod(override val id: String, val name: String, val warn: String): Mod {
    context(mc: MinecraftContext)
    override fun ifFounded(player: PlayerSnapshot, notifier: OpNotificator) {
        player.disconnect(DisconnectReason("<bold>Вы были исключены с сервера из-за мода ${name}</bold><newline>${warn}"))
        notifier.notifyOperators("<red>[!]<reset><gold> ${player.username} зашел на сервер с $name и был позорно кикнут.</gold>")
    }
}
