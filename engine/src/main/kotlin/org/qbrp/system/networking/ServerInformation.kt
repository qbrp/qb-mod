package org.qbrp.system.networking

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.ServerCore
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer
import org.qbrp.system.networking.messaging.NetworkManager

object ServerInformation {
    val COMPOSER = ServerInformationComposer()
    var VIEWER: ClusterViewer? = null

    fun send() = COMPOSER.send()
    fun send(player: ServerPlayerEntity) = COMPOSER.send(player)
    fun build() {
        COMPOSER.build().also {
            VIEWER = it.getData()
        }
    }

}