package org.qbrp.engine.chat.core.messages

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.resources.units.Unit
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.events.MessageHandledEvent
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.system.ServerChatNetworking
import org.qbrp.system.utils.log.Loggers
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.text.toMutableList

open class MessageSender(private val networking: ServerChatNetworking, private val targets: CopyOnWriteArrayList<ServerPlayerEntity> = CopyOnWriteArrayList<ServerPlayerEntity>()) : Sender {
    constructor(networking: ServerChatNetworking, list: List<ServerPlayerEntity>) : this (networking, CopyOnWriteArrayList(list))
    companion object { val logger = Loggers.get("chat", "sending")}
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
        val receivers = targets.filter {
            MessageSendEvent.EVENT.onMessageSend(this, message.copy(), it, networking) == ActionResult.SUCCESS
        }
        if (message.getTags().getComponentData<Boolean>("log") != true) {
            message.getText()
            message.getTagsBuilder().component("log", true)
        }
        MessageHandledEvent.EVENT.invoker().onMessageSend(message, receivers)
    }

    fun filterSenders(predicate: (ServerPlayerEntity) -> Boolean): MessageSender {
        val filteredTargets = targets.filter(predicate).toMutableList()
        return MessageSender(networking, filteredTargets as CopyOnWriteArrayList<ServerPlayerEntity>)
    }

    fun forEachSender(action: (ServerPlayerEntity) -> kotlin.Unit): MessageSender {
        targets.forEach(action)
        return MessageSender(networking, targets)
    }

}