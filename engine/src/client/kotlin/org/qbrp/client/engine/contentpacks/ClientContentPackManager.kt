package org.qbrp.client.engine.contentpacks

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import org.koin.core.component.get
import org.koin.core.module.Module
import org.qbrp.main.core.Core
import org.qbrp.client.core.notifications.ClientNotificationSender
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.util.networking.messages.components.ClusterEntry
import org.qbrp.main.engine.text.components.TextSequence
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor
import org.qbrp.main.network.player.Notification

@Autoload(env = EnvType.CLIENT)
class ClientContentPackManager : EngineModule("server-packs"), ClientContentPacksAPI {
    private val notificationSender = get<ClientNotificationSender>()

    override fun onLoad() {
        createConfig(DownloadConfig())
        ClientLifecycleEvents.CLIENT_STARTED.register {
            val config = requireConfig<DownloadConfig>()
            applyPack(config.host, config.port, config.serverName)
        }
    }

    override fun getKoinModule(): Module = inner<ClientContentPacksAPI>(this) {  }

    override fun applyPack(host: String, port: Int, name: String) {
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