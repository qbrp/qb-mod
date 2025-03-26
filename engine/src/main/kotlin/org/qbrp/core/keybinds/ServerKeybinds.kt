package org.qbrp.core.keybinds

import org.lwjgl.glfw.GLFW
import org.qbrp.engine.Engine
import org.qbrp.system.networking.ServerInformation
import org.qbrp.system.networking.ServerInformationComposer
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.types.ClusterListContent
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.networking.messaging.ServerReceiver
import org.qbrp.system.networking.messaging.ServerReceiverContext

object ServerKeybinds {
    val keybinds: MutableList<ServerKeyBind> = mutableListOf()

    fun registerKeyBind(id: String, defaultKey: Int, name: String) {
        val keybind = ServerKeyBind(id, defaultKey, name)
        registerKeybindReceiver(id)
        keybinds.add(keybind)
    }

    fun registerKeybindReceiver(id: String) {
        ServerReceiver<ServerReceiverContext>("key_bind_$id", Signal::class, { message, context, receiver ->
            val event = ServerKeybindCallback.getOrCreateEvent(id)
            val result = event.invoker().onKeyPress(context.player)
            result.isAccepted
        }).register()
    }

    fun composeServerInfo() {
        val keybinds = ClusterListContent().apply { list = keybinds.map { it.toCluster() } }
        ServerInformation.COMPOSER.component("core.keybinds", keybinds)
    }

}