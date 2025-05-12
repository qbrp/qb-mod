package org.qbrp.system.networking

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer

object ServerInformation {
    val COMPOSER = ServerInformationComposer()
    var VIEWER: ClusterViewer? = null
    val DOWNLOAD_PORT: Int?
        get() = VIEWER?.getComponentData<Int>("engine.web.download_port")


    fun send() = COMPOSER.send()
    fun send(player: ServerPlayerEntity) = COMPOSER.send(player)
    fun build() {
        COMPOSER.build().also {
            VIEWER = it.getData()
        }
    }

}