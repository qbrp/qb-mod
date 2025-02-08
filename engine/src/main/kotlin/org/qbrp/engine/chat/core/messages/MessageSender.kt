package org.qbrp.engine.chat.core.messages

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.system.ServerChatNetworking

class MessageSender(private val networking: ServerChatNetworking, private val targets: MutableList<ServerPlayerEntity>) : Sender {
    fun addTarget(player: ServerPlayerEntity) {
        targets.removeIf { it.uuid == player.uuid }
        targets.add(player)
    }

    fun addTargets(players: List<ServerPlayerEntity>) {
        players.forEach { player ->
            targets.removeIf { it.uuid == player.uuid }
            targets.add(player)
        }
    }

    fun getSenders(): List<String> {
        return targets.map { it.name.string }
    }
    fun isPlayerInTarget(player: ServerPlayerEntity): Boolean = targets.contains(player)

    fun removeTarget(player: ServerPlayerEntity) {
        targets.removeIf { it.uuid == player.uuid }
    }

    override fun send(message: ChatMessage) {
        targets.forEach {
            MessageSendEvent.Companion.EVENT.invoker().onMessageSend(this, message)
            networking.sendMessagePacket(it, message)
        }
    }
}