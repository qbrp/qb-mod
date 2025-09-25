package org.qbrp.main.core.mc.actionbar

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.mc.text.MinecraftText
import org.qbrp.main.core.mc.text.map
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import kotlin.concurrent.fixedRateTimer

@Autoload
class ActionBarModule: EngineModule("action-bar"), ActionBarAPI {
    private val actionBarStatuses = mutableMapOf<ServerPlayerEntity, Text>()
    private val server: MinecraftServer by inject()

    override fun onEnable() = withCoreContext {
        handleActionBarStatus()
    }

    override fun getKoinModule(): Module = onlyApi<ActionBarAPI>(this)

    context(ctx: MinecraftContext)
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

    context(ctx: MinecraftContext)
    private fun sendActionBarMsg(
        player: ServerPlayerEntity,
        message: Text
    ) {
        player.sendMessage(message, true)
    }

    context(ctx: MinecraftContext)
    override fun sendActionBarMsg(
        player: MinecraftPlayerId,
        message: MinecraftText
    ) {
        sendActionBarMsg(player.getEntity(), message.map())
    }

    context(ctx: MinecraftContext)
    override fun setActionBarStatus(
        player: MinecraftPlayerId,
        statusMessage: MinecraftText
    ) {
        val text = statusMessage.map()
        val player = player.getEntity()
        if (text.string == "") sendActionBarMsg(player, text)
        actionBarStatuses[player] = text
    }
}
