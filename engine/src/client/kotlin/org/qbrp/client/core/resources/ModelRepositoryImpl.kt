package org.qbrp.client.core.resources

import net.minecraft.client.util.ModelIdentifier
import org.qbrp.main.core.Core
import org.qbrp.main.engine.assets.contentpacks.build.ModelEntry

class ModelRepositoryImpl(override val ids: MutableList<ModelEntry>): ModelRepository {
    companion object {
        val UNDEFINED = ModelIdentifier(Core.MOD_ID, "undefined", "inventory")
    }

    override fun getResourceLocation(id: String): ModelIdentifier {
        val entry = ids.find { it.id == id }
        return try {
            ModelIdentifier(Core.MOD_ID, entry!!.location, "inventory")
        } catch (e: Exception) {
            return UNDEFINED
        }
    }

    override fun clear() {
        ids.clear()
    }
}