package org.qbrp.engine.client.engine.contentpacks

import org.qbrp.system.modules.ModuleAPI

interface ServerPacksAPI: ModuleAPI {
    fun getCurrentPack(): ServerPack
}