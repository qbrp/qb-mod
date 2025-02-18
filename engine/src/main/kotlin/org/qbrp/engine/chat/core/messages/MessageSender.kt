package org.qbrp.engine.chat.core.messages

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.resources.units.Unit
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.system.ServerChatNetworking
import kotlin.text.toMutableList

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

    fun getReceivers(): List<String> {
        return targets.map { it.name.string }
    }

    fun isPlayerInTarget(player: ServerPlayerEntity): Boolean = targets.contains(player)

    fun removeTarget(player: ServerPlayerEntity) {
        targets.removeIf { it.uuid == player.uuid }
    }

    override fun send(message: ChatMessage) {
        targets.forEach {
            MessageSendEvent.EVENT.invoker().onMessageSend(this, message, it, networking)
            println("Отправление сообщения: $message")
        }
    }

    fun filterSenders(predicate: (ServerPlayerEntity) -> Boolean): MessageSender {
        val filteredTargets = targets.filter(predicate).toMutableList()
        return MessageSender(networking, filteredTargets)
    }

    fun forEachSender(action: (ServerPlayerEntity) -> kotlin.Unit): MessageSender {
        targets.forEach(action)
        return MessageSender(networking, targets)
    }

}