package org.qbrp.main.core.keybinds

import org.koin.core.component.get
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.ServerNetworkContext
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.engine

@Autoload
class ServerKeybinds: EngineModule("keybinds"), KeybindPacketListener {
    companion object { const val CHANNEL = "key_bind" }
    override fun onEnable() {
        get<PacketListenerActivator<ServerNetworkContext>>().run(
            CHANNEL,
            this,
            { buf, ctx -> KeybindActivationPacket(buf, ctx.player.engine) }
        )
    }

    override fun onKeybind(player: PlayerObject, id: KeybindId) {
        withEventContext { with(this) { KeybindCallback(player as ServerPlayerObject).emit(id.toEventId()) } }
    }
}