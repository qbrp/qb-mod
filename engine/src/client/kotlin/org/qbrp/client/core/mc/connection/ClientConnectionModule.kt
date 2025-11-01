package org.qbrp.client.core.mc.connection

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.koin.core.component.inject
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.fabricmc.server.LogicThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT)
class ClientConnectionModule() : EngineModule("client-connection"), ClientConnection {
    private val mainThread by inject<LogicThread>()
    override var isOnServer: Boolean = false

    override fun onEnable() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            isOnServer = true
            mainThread.execute { ConnectEvent(this).emit() }
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            emitDisconnect()
        }
        ClientLoginConnectionEvents.DISCONNECT.register { _, _ ->
            emitDisconnect()
        }
    }

    private fun emitDisconnect() {
        if (!isOnServer) return
        isOnServer = false
        mainThread.execute { DisconnectEvent(this).emit() }
    }
}