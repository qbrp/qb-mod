package org.qbrp.system.networking

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.ServerCore
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messaging.NetworkManager

class ServerInformationComposer: ClusterBuilder() {

    fun send() {
        ServerCore.server.playerManager.playerList.forEach { player -> send(player) }
    }

    fun send(player: ServerPlayerEntity) {
        NetworkManager.sendMessage(player,
        Message(
            SERVER_INFORMATION,
            build()
            )
        )
    }
}