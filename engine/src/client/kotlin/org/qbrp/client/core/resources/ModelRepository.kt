package org.qbrp.client.core.resources

import net.minecraft.client.util.ModelIdentifier
import org.qbrp.main.engine.assets.contentpacks.build.ModelEntry

interface ModelRepository {
    val ids: MutableList<ModelEntry>
    fun getResourceLocation(id: String): ModelIdentifier
    fun clear()
}