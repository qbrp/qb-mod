package org.qbrp.main.core.networking.synchronization

import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.acknowledge.PacketSendTask
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.items.components.ExactTick
import org.qbrp.main.engine.items.networking.SyncListener
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import java.util.LinkedList
import java.util.Queue

abstract class SynchronizerComponent<C : Identifiable>(
    override var state: AssociatedComponentState<C>,
    private val packetSender: ServerPacketSender,
    val playerStorage: ServerPlayerStorage
) : Component<AssociatedComponentState<C>>, Activateable, ExactTick {

    protected data class Task(val channelName: String, val packet: Packet<*>)

    override var enabled: Boolean = false
    var globalSynchronization: Boolean = false

    private val queue: Queue<Task> = LinkedList()
    private var tickCounter: Int = 0

    protected abstract val defaultChannel: String
    protected open val syncInterval = SYNC_INTERVAL

    protected abstract fun createPacket(): Packet<*>
    protected abstract fun getTargetsForSync(): List<ServerPlayerObject>

    override fun tickStateExact() {
        if (!enabled) return

        val players = getTargetsForSync()
        processQueue(players)
        tickCounter++

        if (tickCounter > syncInterval) {
            tickCounter = 0
            onTimer(players)
        }
    }

    open fun onTimer(players: List<ServerPlayerObject>) {
        sendBatch(players)
        if (globalSynchronization) sendGlobal()
    }

    fun queuePacket(channelName: String = defaultChannel, packet: Packet<*> = createPacket()) {
        queue.add(Task(channelName, packet))
    }

    private fun processQueue(players: List<ServerPlayerObject>) {
        while (queue.isNotEmpty()) {
            val task = queue.poll() ?: continue
            getBroadcastTask(task.packet, task.channelName, players)
        }
    }

    fun sendBatch(players: List<ServerPlayerObject>) {
        val packet = createPacket()
        getBroadcastTask(packet, defaultChannel, players)
    }

    fun sendBatch(player: ServerPlayerObject) {
        val packet = createPacket()
        getBroadcastTask(packet, defaultChannel, player).queue()
    }

    fun getBatchTask(player: ServerPlayerObject): PacketSendTask {
        val packet = createPacket()
        return getBroadcastTask(packet, defaultChannel, player)
    }

    open fun sendGlobal() {
        getBroadcastTask(createPacket(), defaultChannel, playerStorage.getAll().toList())
    }

    fun sendPacket(packet: Packet<*>, channel: String, player: ServerPlayerObject) {
        packetSender.sendImmediately(packet, channel, player)
    }

    fun sendPacketWithTask(packet: Packet<*>, channel: String, player: ServerPlayerObject): PacketSendTask {
        return packetSender.send(packet, channel, player)
    }

    private fun getBroadcastTask(packet: Packet<*>, channel: String, players: List<ServerPlayerObject>) {
        players.forEach { getBroadcastTask(packet, channel, it).queue() }
    }

    private fun getBroadcastTask(packet: Packet<*>, channel: String, player: ServerPlayerObject): PacketSendTask {
        val listeners = this@SynchronizerComponent.state.filterIsInstance<SyncListener>()
        listeners.forEach { it.onSync(this, player) }
        return sendPacketWithTask(packet, channel, player)
    }

    companion object {
        private const val SYNC_INTERVAL = 120
    }
}