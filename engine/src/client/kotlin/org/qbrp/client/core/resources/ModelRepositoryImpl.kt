package org.qbrp.client.core.resources

import net.minecraft.client.util.ModelIdentifier
import org.qbrp.main.core.Core
class ModelRepositoryImpl(override val ids: MutableMap<String, String>): ModelRepository {
    companion object {
        val UNDEFINED = ModelIdentifier(Core.MOD_ID, "undefined", "inventory")
    }

    override fun getResourceLocation(id: String): ModelIdentifier {
        return try {
            ModelIdentifier(Core.MOD_ID, ids[id], "inventory")
        } catch (e: Exception) {
            return UNDEFINED
        }
    }

    override fun clear() {
        ids.clear()
    }
}