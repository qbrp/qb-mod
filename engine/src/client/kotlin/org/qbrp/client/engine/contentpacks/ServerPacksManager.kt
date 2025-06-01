package org.qbrp.client.engine.contentpacks

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import org.koin.core.component.get
import org.koin.core.module.Module
import org.qbrp.main.core.Core
import org.qbrp.client.ClientCore
import org.qbrp.client.core.networking.info.ServerInfoReader
import org.qbrp.client.engine.ClientEngine
import org.qbrp.client.engine.auth.ClientAuthEvent
import org.qbrp.client.engine.notifications.ClientNotificationsAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import org.qbrp.main.engine.assets.web.WebServerModule

@Autoload(env = EnvType.CLIENT)
class ServerPacksManager: QbModule("server-packs"), ServerPacksAPI {
    init {
        ClientEngine.isApiAvailable<ClientNotificationsAPI>()
    }

    companion object {
        val CONTENTPACKS_ENABLED = ClusterEntry<Boolean>("engine.contentpacks.enabled")
    }

    override fun onLoad() {
        createConfig(DownloadConfig())
        ClientLifecycleEvents.CLIENT_STARTED.register {
            val config = requireConfig<DownloadConfig>()
            applyPack(config.host, config.port, config.serverName)
        }
        val info = get<ServerInfoReader>()
        ClientAuthEvent.EVENT.register { handler ->
            val contentPacksEnabled = info.VIEWER.getEntry(CONTENTPACKS_ENABLED)!!
            val port = info.VIEWER.getEntry(WebServerModule.DOWNLOAD_PORT)!!
            val serverName = info.VIEWER.getEntry(Core.SERVER_NAME)!!
            val ip = ClientCore.getServerIp()!!
            if (contentPacksEnabled) applyPack(ip, port, serverName,)
        }
    }

    override fun getKoinModule(): Module = inner<ServerPacksAPI>(this) {  }

    fun applyPack(host: String, port: Int, name: String) {
        val notifications = get<ClientNotificationsAPI>()
        try {
            requireServerPack(host, port, name) {
                ServerContentPackEvents.ON_APPLY.invoker().onApply(it)
                    notifications.sendSystemMessage(
                    "Ассеты", "Применен набор ассетов сервера <aqua>${name}"
                )
            }
        } catch (e: Exception) {
            notifications.sendSystemMessage("Ошибка загрузки", e.message.toString())
            e.printStackTrace()
        }
    }

    fun requireServerPack(host: String, port: Int, name: String, onComplete: (ServerPack) -> Unit) {
        val serverHost = "$host:$port"
        val downloadUrl = "http://$serverHost"
        val downloadKey = PackDownloadKey(name, downloadUrl, serverHost)
        val reference = VersionedPackDownloadReference(downloadKey)
        return Core.ASSETS.getOrLoadAsync(reference, { onComplete(it) }, { throw it })
    }

    override fun getCurrentPack(): ServerPack {
        TODO()
    }

    override fun getAPI(): ServerPacksAPI = this
}