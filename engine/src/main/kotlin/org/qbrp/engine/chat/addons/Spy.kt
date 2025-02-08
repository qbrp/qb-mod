package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.chat.core.system.ServerChatNetworking

class Spy(val server: MinecraftServer, val networking: ServerChatNetworking) {
    fun getSpyPlayers(): List<ServerPlayerEntity> {
        return server.playerManager.playerList
    }
    init {
        MessageSenderPipeline.EVENT.register { message, sender ->
            val spyPlayers = getSpyPlayers()
                .filterNot { it.name.string == message.authorName }
                .toMutableList()
            if (!spyPlayers.isEmpty() && sender.isPlayerInTarget(message.getAuthorEntity(server.playerManager)!!) ) {
                val spySender = MessageSender(networking, spyPlayers)
                val spyMessage = message.copy().apply {
                    text = "&6[S]&r $text"
                }
                spySender.send(spyMessage)
            }
            ActionResult.PASS
        }
    }
}