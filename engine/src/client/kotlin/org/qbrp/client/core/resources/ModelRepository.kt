package org.qbrp.client.core.resources

import net.minecraft.client.util.ModelIdentifier

interface ModelRepository {
    val ids: MutableMap<String, String>
    fun getResourceLocation(id: String): ModelIdentifier
    fun clear()
}