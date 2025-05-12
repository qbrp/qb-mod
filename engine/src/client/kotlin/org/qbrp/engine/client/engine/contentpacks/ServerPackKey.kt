package org.qbrp.engine.client.engine.contentpacks

import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.common.Key

class ServerPackKey(val host: String): Key {
    override fun getId(): String {
        return "${FileSystem.CLIENT_SERVER_PACKS.path}/$host"
    }
}