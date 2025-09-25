package core

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.acknowledge.PacketSendTask
import org.qbrp.main.core.utils.log.LoggerUtil

class ServerPacketSenderMock: ServerPacketSender {
    private val sendTaskLogger = LoggerUtil.get("networking")
    override fun sendImmediately(
        packet: Packet<*>,
        channelName: String,
        player: PlayerReference
    ) {
    }

    override fun sendImmediately(
        packet: Packet<*>,
        channelName: String,
        player: ServerPlayerEntity
    ) {
    }

    override fun send(
        packet: Packet<*>,
        channelName: String,
        player: PlayerReference
    ): PacketSendTask {
        return PacketSendTaskMock(IDGenerator.nextId(), packet, player)
    }

    inner class PacketSendTaskMock(override val id: Long, private val packet: Packet<*>, private val player: PlayerReference): PacketSendTask {
        var log: Boolean = false
        var logName: String = ""

        override suspend fun waitAcknowledge(retryAttempts: Int, retryTime: Int) {
            sendTaskLogger.log("Подтвержден $logName ($id)")
        }

        override suspend fun log(name: String): PacketSendTask {
            logName = name
            log = true
            return this
        }

        override fun queue(): PacketSendTask {
            if (log) sendTaskLogger.log("Отправлен ${logName} ($id)")
            return this
        }
    }
}