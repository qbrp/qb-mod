package org.qbrp.system.networking.http

import klite.AssetsHandler
import klite.Server
import org.qbrp.core.resources.ServerResources
import java.net.InetSocketAddress
import java.nio.file.Path
import kotlin.io.path.pathString

class WebServer {

    val server: Server = Server(listen = InetSocketAddress(ServerResources.getConfig().http.port))

    fun start() {
        server.assets("/", AssetsHandler(Path.of(ServerResources.getRootBranch().resources.path.pathString)))
        server.start()
    }

    fun stop() {
        server.stop()
    }

}