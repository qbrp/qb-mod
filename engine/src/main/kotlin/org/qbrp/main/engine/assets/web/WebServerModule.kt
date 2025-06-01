package org.qbrp.main.engine.assets.web

import klite.AssetsHandler
import klite.Server
import org.koin.core.component.get
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.assets.resourcepack.versioning.ResourcePackVersionsAPI
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.info.ServerInfoAPI
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import java.net.InetSocketAddress

@Autoload(LoadPriority.MODULE - 2)
class WebServerModule: QbModule("web-server") {
    val port = get<ServerConfigData>().http.port
    val server: Server = Server(listen = InetSocketAddress(port))

    init {
        dependsOn { Engine.isApiAvailable<ResourcePackVersionsAPI>() }
    }

    companion object {
        val DOWNLOAD_PORT = ClusterEntry<Int>("engine.web.download-port")
    }

    override fun onLoad() {
        start()
        get<ServerInfoAPI>().COMPOSER.component(DOWNLOAD_PORT, port)
    }

    fun validateVersionPath(path: String): String {
        return "${path
            .replace("\\", "/")}/pack.zip"
    }

    fun start() {
        val resourcePacksApi = requireApi<ResourcePackVersionsAPI>()
        server.assets("/", AssetsHandler(FileSystem.HTTP_SERVER_ASSETS.toPath()))
        server.apply {
            context("/update").get("/checkVersion/:version") {
                val version = path("version") ?: error("Not stated version")
                val lastVersion = resourcePacksApi.getVersion()

                if (version != lastVersion) {
                    val patchExists = resourcePacksApi.isPatchExists(version, lastVersion)
                    return@get validateVersionPath(
                        if (!patchExists) "contentpacks/$lastVersion"
                             else "contentpacks-patches/$version-$lastVersion"
                    )
                }
                return@get "up-to-date"
            }
        }

        server.start()
    }

    fun stop() {
        server.stop()
    }

}