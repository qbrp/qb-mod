package org.qbrp.client.engine.contentpacks

import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.Key

class ServerPackKey(val host: String): Key {
    override fun getId(): String {
        return "${FileSystem.CLIENT_SERVER_PACKS.path}/$host"
    }
}