package org.qbrp.main.network.transport

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.Channel as KotlinxChannel
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.fabricmc.player.asReference
import kotlin.collections.set

data class AcknowledgePacket(val id: Long) : Packet

internal fun NetworkChannel(id: String) = NetworkChannel<AcknowledgePacket>(
    id,
    PacketCodec.Binary(
        { AcknowledgePacket(readLong()) },
        { writeLong(it.id) }
    )
)

val ACKNOWLEDGE_REQUEST_CHANNEL = NetworkChannel("acknowledge_request")
    .wrapEndpoint()
val ACKNOWLEDGE_CONFIRM_CHANNEL = NetworkChannel("acknowledge_confirm")
    .wrapEndpoint()

fun AcknowledgeProgram.startListening() {
    ACKNOWLEDGE_CONFIRM_CHANNEL
        .registerReceiver { confirm(id) }
}

class AcknowledgeProgram {
    private val listeners: MutableMap<Long, () -> Unit> = mutableMapOf()
    private var lock = Any()

    fun addListener(id: Long, onReceive: () -> Unit) = synchronized(lock) {
        listeners[id] = onReceive
    }

    fun removeListener(id: Long) = synchronized(lock) {
        listeners.remove(id)
    }

    internal fun confirm(id: Long) {
        listeners.remove(id)?.invoke()
    }
}

data class PacketNotAcknowledgedException(
    val packet: Packet,
    val id: Long
) : RuntimeException("Пакет ${packet::class.simpleName} не подтвержден. ID: $id ")

class ServerAcknowledgeTask(
    private val id: Long,
    private val packet: Packet,
    private val entity: ServerPlayerEntity,
    private val acknowledgeProgram: AcknowledgeProgram,
    val retryAttempts: Int,
    val retryTime: Int,
) : AcknowledgeTask {
    private var onTimeout: () -> Unit = {
        throw PacketNotAcknowledgedException(packet, id)
    }

    override fun onTimeout(b: () -> Unit): AcknowledgeTask {
        onTimeout = b
        return this
    }

    override suspend fun run() = withContext(Dispatchers.IO) {
        val receiveChannel = KotlinxChannel<Unit>(capacity = 1)
        var timeout = false
        acknowledgeProgram.addListener(id) { receiveChannel.trySend(Unit) }

        val senderJob = launch {
            var attempts = 0
            while (isActive && attempts < retryAttempts) {
                sendS2CAcknowledgePacket(id, entity)
                delay(retryTime.toLong())
                attempts++
            }
            if (!timeout) {
                timeout = true
                onTimeout()
                receiveChannel.close()
            }
        }

        try {
            receiveChannel.receive() // ждём подтверждение
        } catch (e: ClosedReceiveChannelException) {
        } finally {
            timeout = true
            acknowledgeProgram.removeListener(id)
            receiveChannel.close()
            senderJob.cancel() // останавливаем отправку
        }
    }

}

internal fun sendS2CAcknowledgePacket(id: Long, player: ServerPlayerEntity) {
    ACKNOWLEDGE_REQUEST_CHANNEL
        .sendS2C(AcknowledgePacket(id), player.asReference())
}