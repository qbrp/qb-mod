package org.qbrp.engine.assets.web

import klite.AssetsHandler
import klite.Server
import org.qbrp.core.assets.FileSystem
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.Engine
import org.qbrp.engine.assets.resourcepack.versioning.ResourcePackVersionsAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.InfoNames
import org.qbrp.system.networking.ServerInformation
import java.net.InetSocketAddress

@Autoload(LoadPriority.LOWEST)
class WebServerModule: QbModule("web-server") {
    val port = ServerResources.getConfig().http.port
    val server: Server = Server(listen = InetSocketAddress(port))

    init {
        dependsOn { Engine.isApiAvailable<ResourcePackVersionsAPI>() }
    }

    override fun onLoad() {
        start()
        ServerInformation.COMPOSER.component(InfoNames.DOWNLOAD_PORT, port)
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