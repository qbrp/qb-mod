package org.qbrp.main.core.mc.actionbar

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.koin.core.module.Module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import kotlin.concurrent.fixedRateTimer

@Autoload
class ActionBarModule: QbModule("action-bar"), ActionBarAPI {
    private val actionBarStatuses = mutableMapOf<ServerPlayerEntity, Text>()

    override fun onEnable() {
        handleActionBarStatus()
    }

    override fun getKoinModule(): Module = onlyApi<ActionBarAPI>(this)

    override fun sendActionBarMsg(player: ServerPlayerEntity, message: Text) {
        player.sendMessage(message, true)
    }

    override fun setActionBarStatus(
        player: ServerPlayerEntity,
        statusMessage: String
    ) {
        if (statusMessage == "") sendActionBarMsg(player, Text.of(""))
        actionBarStatuses[player] = statusMessage.asMiniMessage()
    }

    private fun handleActionBarStatus() {
        fixedRateTimer(
            name = "[qbrp/View] [ActionBarStatus]",
            initialDelay = 0,
            period = 400,
            daemon = true
        ) {
            actionBarStatuses.forEach { key, value ->
                if (value.string != "") {
                    sendActionBarMsg(key, value)
                }
            }
        }
    }
}
