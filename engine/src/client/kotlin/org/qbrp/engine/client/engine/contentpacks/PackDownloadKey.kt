package org.qbrp.engine.client.engine.contentpacks

import org.qbrp.core.assets.FileSystem
import org.qbrp.engine.client.core.assets.DownloadKey

class PackDownloadKey(val serverName: String, downloadUrl: String, val serverHost: String = serverName)
    : DownloadKey(serverName, downloadUrl) {
    override fun getId(): String {
        return "${FileSystem.CLIENT_SERVER_PACKS}/${path.replace(":", "-")}"
    }
}