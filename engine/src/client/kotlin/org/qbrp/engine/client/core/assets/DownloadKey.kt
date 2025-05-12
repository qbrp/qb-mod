package org.qbrp.engine.client.core.assets

import org.qbrp.core.assets.common.Key
import java.net.InetAddress
import java.net.NoRouteToHostException

open class DownloadKey(val path: String, val downloadUrl: String): Key {
    constructor(path: Key, host: String) : this(path.getId(), host)

    override fun getId(): String {
        return "qbrp/$path"
    }

    fun validateHost() {
        try {
            val address = InetAddress.getByName(downloadUrl)
            if (!address.isReachable(5000)) {
                throw NoRouteToHostException("Download link $downloadUrl is not reachable")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to validate host: $downloadUrl. Error: ${e.message}")
        }
    }
}