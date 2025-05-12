package org.qbrp.engine.client.engine.contentpacks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.qbrp.core.assets.Assets
import org.qbrp.engine.Engine
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.assets.DownloadKey
import org.qbrp.engine.client.core.events.ClientAuthEvent
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.ServerInformation

@Autoload(env = EnvType.CLIENT)
class ServerPacksManager: QbModule("server-packs") {
    override fun load() {
        ClientLifecycleEvents.CLIENT_STARTED.register {
            val res = ClientResources.root.configData.resources
            //applyPack(res.host, res.port)
        }
        ClientAuthEvent.EVENT.register { handler ->
            applyPack(EngineClient.getServerIp()!!, ServerInformation.DOWNLOAD_PORT!!)
        }
    }

    fun applyPack(host: String, port: Int) {
        try {
            requireServerPack(host, port) {
                ServerContentPackEvents.ON_APPLY.invoker().onApply(it)
                EngineClient.notificationsManager.sendSystemMessage(
                    "<green>Ассеты загружены", "Успешно загружен набор ассетов сервера <aqua>$host"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun requireServerPack(host: String, port: Int, onComplete: (ServerPack) -> Unit) {
        val serverHost = "$host:$port"
        val downloadUrl = "http://$serverHost"
        val downloadKey = PackDownloadKey(host, downloadUrl, host, serverHost)
        val reference = VersionedPackDownloadReference(downloadKey)
        return Assets.getOrLoadAsync(reference, { onComplete(it) }, { throw it })
    }
}