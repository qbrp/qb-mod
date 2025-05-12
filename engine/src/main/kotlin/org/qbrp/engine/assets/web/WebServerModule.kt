package org.qbrp.engine.assets.web

import klite.AssetsHandler
import klite.Server
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.Engine
import org.qbrp.engine.assets.resourcepack.versioning.ResourcePackVersionsAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.ServerInformation
import java.net.InetSocketAddress

@Autoload(LoadPriority.LOWEST)
class WebServerModule: QbModule("web-server") {
    val port = ServerResources.getConfig().http.port
    val server: Server = Server(listen = InetSocketAddress(port))

    init {
        dependsOn { Engine.isApiAvailable<ResourcePackVersionsAPI>() }
    }

    override fun load() {
        start()
        ServerInformation.COMPOSER.component("engine.web.download_port", port)
    }

    fun validateVersionPath(path: String): String {
        return "${path
            .replace("\\", "/")
            .replace("qbrp/temp/pack-patches/", "")}/pack.zip"
    }

    fun start() {
        val resourcePacksApi = requireApi<ResourcePackVersionsAPI>()
        server.assets("/", AssetsHandler(resourcePacksApi.getPacksFile().toPath()))
        server.apply {
            context("/update").get("/checkVersion/:version") {
                val version = path("version") ?: error("Not stated version")
                val lastVersion = resourcePacksApi.getLatestVersion()

                if (version != lastVersion) {
                    val patchExists = resourcePacksApi.isPatchExists(version, lastVersion ?: "NONE")
                    return@get validateVersionPath(
                        if (!patchExists) resourcePacksApi.getLatestVersionFile().path else "$version-$lastVersion"
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