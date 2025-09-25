package org.qbrp.client.engine.contentpacks

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import org.koin.core.component.get
import org.koin.core.module.Module
import org.qbrp.main.core.Core
import org.qbrp.client.ClientCore
import org.qbrp.client.core.networking.info.ServerInfoReader
import org.qbrp.client.core.notifications.ClientNotificationSender
import org.qbrp.client.engine.auth.ClientAuthEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.notifications.entity.Notification
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import org.qbrp.main.engine.assets.web.WebServerModule
import org.qbrp.main.engine.text.components.TextSequence
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor

@Autoload(env = EnvType.CLIENT)
class ClientContentPackManager: EngineModule("server-packs"), ClientContentPacksAPI {
    private val notificationSender = get<ClientNotificationSender>()

    override fun onLoad() {
        createConfig(DownloadConfig())
        ClientLifecycleEvents.CLIENT_STARTED.register {
            val config = requireConfig<DownloadConfig>()
            applyPack(config.host, config.port, config.serverName)
        }
        val info = get<ServerInfoReader>()
        ClientAuthEvent.EVENT.register { session ->
            val contentPacksEnabled = info.VIEWER.getEntry(CONTENTPACKS_ENABLED)!!
            val port = info.VIEWER.getEntry(WebServerModule.DOWNLOAD_PORT)!!
            val serverName = info.VIEWER.getEntry(Core.SERVER_NAME)!!
            val ip = ClientCore.getServerIp()!!
            if (contentPacksEnabled) applyPack(ip, port, serverName)
        }
    }

    override fun getKoinModule(): Module = inner<ClientContentPacksAPI>(this) {  }

    fun applyPack(host: String, port: Int, name: String) {
        try {
            requireServerPack(host, port, name) {
                ContentPackEvents.ON_APPLY.invoker().onApply(it)
                val text = TextSequence.Builder()
                    .literal("Применен набор ассетов сервера ")
                    .literal(name, Style.ofColor(TextColor.AQUA))
                    .build()
                notificationSender.send(Notification.info("Ассеты", text))
            }
        } catch (e: Exception) {
            val notification = Notification.error("Ошибка", "Загрузка ассетов", e)
            notificationSender.send(notification)
            e.printStackTrace()
        }
    }

    fun requireServerPack(host: String, port: Int, name: String, onComplete: (ClientContentPack) -> Unit) {
        val serverHost = "$host:$port"
        val downloadUrl = "http://$serverHost"
        val downloadKey = PackDownloadKey(name, downloadUrl, serverHost)
        val reference = VersionedPackDownloadReference(downloadKey)
        return Core.ASSETS.loadAsync(reference, { onComplete(it) }, { throw it })
    }

    override fun getCurrentPack(): ClientContentPack {
        TODO()
    }

    override fun getAPI(): ClientContentPacksAPI = this

    companion object {
        val CONTENTPACKS_ENABLED = ClusterEntry<Boolean>("engine.contentpacks.enabled")
    }
}