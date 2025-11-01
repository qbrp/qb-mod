package org.qbrp.main.network.transport

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.util.IDGenerator
import org.qbrp.main.network.ServerPacketTransportProgram

// Tasks

interface PacketSendTask {
    val id: Long
    fun queue(): PacketSendTask
    suspend fun requestAcknowledge(retryAttempts: Int = 10, retryTime: Int = 500)
    fun withAcknowledge(retryAttempts: Int = 10, retryTime: Int = 500): AcknowledgeTask
}

interface AcknowledgeTask {
    fun onTimeout(b: () -> Unit): AcknowledgeTask
    suspend fun run()
}

class ServerPacketSendTask<P : Packet>(
    override val id: Long,
    private val packet: P,
    private val channel: NetworkChannel<P>,
    private val transportProgram: ServerPacketTransportProgram,
    private val acknowledgeProgram: AcknowledgeProgram,
    private val entity: ServerPlayerEntity
) : PacketSendTask {
    override fun queue(): PacketSendTask {
        transportProgram.queueSendTask(entity, packet, channel, id)
        return this
    }

    override suspend fun requestAcknowledge(
        retryAttempts: Int,
        retryTime: Int
    ) {
        withAcknowledge(retryAttempts, retryTime)
            .also { it.run() }
    }

    override fun withAcknowledge(
        retryAttempts: Int,
        retryTime: Int
    ): ServerAcknowledgeTask {
        return ServerAcknowledgeTask(
            id,
            packet,
            entity,
            acknowledgeProgram,
            retryAttempts,
            retryTime
        )
    }
}

// Methods

inline fun <reified P : Packet> ServerPacketTransportProgram.sendS2CPacket(
    packet: P,
    player: PlayerReference,
) {
    sendS2CPacket(packet, NetworkChannel(), player)
}

context(server: ServerDependencies)
fun <P : Packet> PlayerReference.sendPacket(packet: P, channel: NetworkChannel<P>) {
    server.transportProgram.sendS2CPacket(packet, channel, this)
}

context(server: ServerDependencies)
inline fun <reified P : Packet> PlayerReference.sendPacket(packet: P) {
    sendPacket(packet, NetworkChannel())
}

context(server: ServerDependencies)
fun <P : Packet> PlayerReference.taskPacket(packet: P, channel: NetworkChannel<P>): PacketSendTask {
    return server.transportProgram.taskS2CPacket(packet, channel, this)
}


context(server: ServerDependencies)
inline fun <reified P : Packet> PlayerReference.taskPacket(packet: P): PacketSendTask {
    return taskPacket(packet, NetworkChannel())
}

// Minecraft

internal fun <P : Packet> sendS2CPacketMc(
    entity: ServerPlayerEntity,
    packet: P,
    codec: PacketCodec<P>,
    channel: Identifier,
    id: Long = IDGenerator.nextId()
) {
    val buf = PacketByteBuf(Unpooled.buffer())
    buf.writeLong(id)
    serializePacket(buf, packet, codec)
    ServerPlayNetworking.send(entity, channel, buf)
}