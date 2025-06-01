package org.qbrp.client.engine.contentpacks

import org.qbrp.main.core.modules.ModuleAPI

interface ServerPacksAPI: ModuleAPI {
    fun getCurrentPack(): ServerPack
}