package org.qbrp.main.engine.chat.core.messages

import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.chat.core.events.MessageHandledEvent
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.engine.chat.core.system.ServerChatNetworking
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.util.PlayerId
import java.util.concurrent.ConcurrentHashMap

open class MessageSender(private val networking: ServerChatNetworking) : Sender {
    constructor(
        networking: ServerChatNetworking,
        list: List<ServerPlayerObject>
    ) : this(networking) {
        targets.putAll(list.associateBy { it.playerId })
    }
    companion object { val logger = LoggerUtil.get("chat", "sending")}

    private val targets: ConcurrentHashMap<PlayerId, ServerPlayerObject> = ConcurrentHashMap()

    fun addTarget(target: ServerPlayerObject) {
        targets[target.playerId] = target
    }

    fun addTargets(players: Collection<ServerPlayerObject>) {
        players.forEach { player -> addTarget(player) }
    }

    fun getReceivers(): List<Username> {
        return targets.map { it.value.username }
    }

    fun isPlayerInTarget(player: ServerPlayerObject): Boolean = targets.contains(player)

    fun removeTarget(target: ServerPlayerObject) {
        targets.remove(target.playerId)
    }

    override fun send(message: ChatMessage) {
        val receivers = targets.filter {
            MessageSendEvent.EVENT.onMessageSend(this, message.copy(), it.value, networking) == ActionResult.SUCCESS
        }
        if (message.getTags().getComponentData<Boolean>("log") != true) {
            message.getText()
            message.getTagsBuilder().component("log", true)
        }
        MessageHandledEvent.EVENT.invoker().onMessageSend(message, receivers.values.toList())
    }

    fun filterSenders(predicate: (ServerPlayerObject) -> Boolean): MessageSender {
        val filteredTargets = targets.values.filter { predicate(it) }
        return MessageSender(networking, filteredTargets)
    }

    fun forEachSender(action: (ServerPlayerObject) -> Unit): MessageSender {
        targets.forEach { action(it.value) }
        return MessageSender(networking, targets.values.toList())
    }

}