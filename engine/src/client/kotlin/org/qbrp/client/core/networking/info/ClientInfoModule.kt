package org.qbrp.client.core.networking.info

import net.fabricmc.api.EnvType
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.info.ServerInformationGetEvent
import org.qbrp.main.core.utils.networking.messages.Messages.SERVER_INFORMATION
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

@Autoload(Priorities.LOWEST, env = EnvType.CLIENT)
class ClientInfoModule: EngineModule("client-server-info"), ServerInfoReader {
    override val VIEWER: ClusterViewer
        get() = _viewer

    private var _viewer = ClusterViewer()

    override fun getKoinModule() = onlyApi<ServerInfoReader>(this)

    override fun onEnable() {
        ClientReceiver(SERVER_INFORMATION, Cluster::class) { message, context, receiver ->
            ServerInformationGetEvent.EVENT.invoker().event(message.getContent())
            _viewer = message.getContent()
            true
        }.register()
    }
}