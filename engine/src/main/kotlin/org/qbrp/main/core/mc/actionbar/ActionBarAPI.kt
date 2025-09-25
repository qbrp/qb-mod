package org.qbrp.main.core.mc.actionbar

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.text.MinecraftText
import org.qbrp.main.core.mc.text.MiniMessageText

interface ActionBarAPI {
    context(ctx: MinecraftContext) fun sendActionBarMsg(player: MinecraftPlayerId, message: MinecraftText)
    context(ctx: MinecraftContext) fun setActionBarStatus(player: MinecraftPlayerId, statusMessage: MinecraftText)
}