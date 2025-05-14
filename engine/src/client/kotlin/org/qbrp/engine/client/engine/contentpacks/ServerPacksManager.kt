package org.qbrp.engine.client.engine.contentpacks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.assets.Assets
import org.qbrp.engine.Engine
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.assets.DownloadKey
import org.qbrp.engine.client.core.events.ClientAuthEvent
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.ServerInformation
import kotlin.math.sin

@Autoload(env = EnvType.CLIENT)
class ServerPacksManager: QbModule("server-packs"), ServerPacksAPI {
    override fun onLoad() {
        createConfig(DownloadConfig())
        ClientLifecycleEvents.CLIENT_STARTED.register {
            val config = requireConfig<DownloadConfig>()
            applyPack(config.host, config.port, config.serverName)
        }
        ClientAuthEvent.EVENT.register { handler ->
            if (ServerInformation.CONTENTPACKS_ENABLED) {
                applyPack(
                    EngineClient.getServerIp()!!,
                    ServerInformation.DOWNLOAD_PORT!!,
                    ServerInformation.SERVER_NAME!!
                )
            }
        }
    }

    fun applyPack(host: String, port: Int, name: String) {
        try {
            requireServerPack(host, port, name) {
                ServerContentPackEvents.ON_APPLY.invoker().onApply(it)
                EngineClient.notificationsManager.sendSystemMessage(
                    "Ассеты", "Применен набор ассетов сервера <aqua>${name}"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun requireServerPack(host: String, port: Int, name: String, onComplete: (ServerPack) -> Unit) {
        val serverHost = "$host:$port"
        val downloadUrl = "http://$serverHost"
        val downloadKey = PackDownloadKey(name, downloadUrl, serverHost)
        val reference = VersionedPackDownloadReference(downloadKey)
        return Assets.getOrLoadAsync(reference, { onComplete(it) }, { throw it })
    }

    override fun getCurrentPack(): ServerPack {
        TODO()
    }

    override fun getAPI(): ServerPacksAPI = this
}