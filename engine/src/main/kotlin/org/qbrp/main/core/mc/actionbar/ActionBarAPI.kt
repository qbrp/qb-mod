package org.qbrp.main.core.mc.actionbar

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

interface ActionBarAPI {
    fun sendActionBarMsg(player: ServerPlayerEntity, message: Text)
    fun setActionBarStatus(player: ServerPlayerEntity, statusMessage: String)
}