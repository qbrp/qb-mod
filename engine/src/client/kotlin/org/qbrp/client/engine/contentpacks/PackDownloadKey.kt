package org.qbrp.client.engine.contentpacks

import org.qbrp.main.core.assets.FileSystem

class PackDownloadKey(val serverName: String, downloadUrl: String, val serverHost: String = serverName)
    : DownloadKey(serverName, downloadUrl) {
    override fun getId(): String {
        return "${FileSystem.CLIENT_SERVER_PACKS}/${path.replace(":", "-")}"
    }
}