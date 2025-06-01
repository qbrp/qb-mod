package org.qbrp.client.engine.contentpacks

import org.qbrp.main.core.modules.ModuleAPI

interface ClientContentPacksAPI: ModuleAPI {
    fun getCurrentPack(): ClientContentPack
}