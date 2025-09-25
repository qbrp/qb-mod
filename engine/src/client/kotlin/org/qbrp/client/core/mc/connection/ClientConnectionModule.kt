package org.qbrp.client.core.mc.connection

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.koin.core.component.inject
import org.qbrp.client.core.mc.connection.event.ConnectEvent
import org.qbrp.client.core.mc.connection.event.DisconnectEvent
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT)
class ClientConnectionModule() : EngineModule("client-connection"), ClientConnection {
    private val mainThread by inject<ServerThread>()
    override var isOnServer: Boolean = false

    override fun onEnable() = withEventContext {
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

    context(ctx: EventContext)
    private fun emitDisconnect() {
        if (!isOnServer) return
        isOnServer = false
        mainThread.execute { DisconnectEvent(this).emit() }
    }
}