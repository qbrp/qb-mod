package org.qbrp.view.hud

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.system.utils.format.Format.formatMinecraft
import kotlin.concurrent.fixedRateTimer

class VanillaHud: ActionBarHud {
    private val actionBarStatuses = mutableMapOf<ServerPlayerEntity, Text>()

    init {
        handleActionBarStatus()
    }

    override fun sendActionBarMsg(player: ServerPlayerEntity, message: Text) {
        player.sendMessage(message, true)
    }

    override fun setActionBarStatus(
        player: ServerPlayerEntity,
        statusMessage: String
    ) {
        if (statusMessage == "") sendActionBarMsg(player, "".formatMinecraft())
        actionBarStatuses[player] = statusMessage.formatMinecraft()
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