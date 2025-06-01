package org.qbrp.main.core.keybinds

import org.koin.core.component.get
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.info.ServerInfoAPI
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import org.qbrp.main.core.utils.networking.messages.types.ClusterListContent
import org.qbrp.main.core.utils.networking.messages.types.Signal
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.engine.ModInitializedEvent

@Autoload
class ServerKeybinds: QbModule("keybinds"), ServerKeybindsAPI {
    val keybinds: MutableList<ServerKeyBind> = mutableListOf()

    companion object {
        val KEYBINDS_ENTRY = ClusterEntry<MutableList<ServerKeyBind>>("core.keybinds")
    }

    override fun onLoad() {
        ModInitializedEvent.EVENT.register {
            composeServerKeybindsInfo()
        }
    }

    override fun getKoinModule() = inner<ServerKeybindsAPI>(this) {  }

    override fun registerKeyBind(id: String, defaultKey: Int, name: String) {
        val keybind = ServerKeyBind(id, defaultKey, name)
        registerKeybindReceiver(id)
        keybinds.add(keybind)
    }

    override fun registerKeybindReceiver(id: String) {
        ServerReceiver<ServerReceiverContext>("key_bind_$id", Signal::class, { message, context, receiver ->
            val event = ServerKeybindCallback.getOrCreateEvent(id)
            val result = event.invoker().onKeyPress(context.player)
            result.isAccepted
        }).register()
    }

    override fun composeServerKeybindsInfo() {
        val keybinds = ClusterListContent().apply { list = keybinds.map { it.toCluster() } }
        get<ServerInfoAPI>().COMPOSER.component(KEYBINDS_ENTRY, keybinds)
    }
}