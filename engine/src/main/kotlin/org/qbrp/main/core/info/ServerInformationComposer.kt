package org.qbrp.main.core.info

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.Core
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil

class ServerInformationComposer: ClusterBuilder() {
    fun send() {
        Core.server.playerManager.playerList.forEach { player -> send(player) }
    }

    fun send(player: ServerPlayerEntity) {
        NetworkUtil.sendMessage(player,
        Message(
            SERVER_INFORMATION,
            build()
            )
        )
    }
}