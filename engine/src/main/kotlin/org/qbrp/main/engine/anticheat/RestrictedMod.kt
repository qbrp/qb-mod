package org.qbrp.main.engine.anticheat

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.engine.anticheat.Anticheat.Companion.sendToOps
import org.qbrp.main.core.utils.format.Format.asMiniMessage

class RestrictedMod(override val id: String, val name: String, val warn: String): Mod {
    override fun ifFounded(player: ServerPlayerEntity) {
        player.networkHandler.disconnect("<red><bold>Вы были исключены с сервера из-за мода ${name}</bold><newline>${warn}</red>".asMiniMessage())
        sendToOps("<red>[!]<reset><gold> ${player.name.string} зашел на сервер с $name и был позорно кикнут.</gold>")
    }
}
