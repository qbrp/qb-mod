package org.qbrp.main.core.info

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.ApplicationLoader
import org.qbrp.main.core.Core
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil

class ServerInformationComposer(private val server: MinecraftServer): ClusterBuilder() {
    fun send() {
        if (ApplicationLoader.initialized) server.playerManager.playerList.forEach { player -> send(player) }
    }

    fun send(player: ServerPlayerEntity) {
        NetworkUtil.sendMessage(
            Message(
                SERVER_INFORMATION,
                build()
                ),
            player
        )
    }
}