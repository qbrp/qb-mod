package org.qbrp.view.hud

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.system.networking.messages.Message

interface ActionBarHud {
    fun sendActionBarMsg(player: ServerPlayerEntity, message: Text)
    fun setActionBarStatus(player: ServerPlayerEntity, statusMessage: String)
}