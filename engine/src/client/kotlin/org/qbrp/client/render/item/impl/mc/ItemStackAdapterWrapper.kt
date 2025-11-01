package org.qbrp.client.render.item.impl.mc

import net.minecraft.item.ItemStack
import org.qbrp.client.core.mc.ABSTRACT_ITEM
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.main.core.fabricmc.getItem
import org.qbrp.main.engine.items.model.ItemObject

class ItemStackAdapterWrapper(
    private val maxCacheSize: Int = 256
) {
    companion object {
        const val MODEL_TAG = "QbrpModel"
    }

    private val definition by lazy { getItem(ABSTRACT_ITEM) }

    private val templateCache = object : LinkedHashMap<String, ItemStack>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ItemStack>): Boolean {
            return size > maxCacheSize
        }
    }

    fun wrapItemStack(item: ItemDisplay): ItemStack {
        val key = item.model.modelId
        val template = synchronized(templateCache) {
            templateCache[key] ?: createTemplate(key).also { templateCache[key] = it }
        }
        return template.copy()
    }

    private fun createTemplate(modelId: String): ItemStack =
        ItemStack(definition).apply {
            orCreateNbt.putString(MODEL_TAG, modelId)
        }

    fun invalidateAll() {
        synchronized(templateCache) { templateCache.clear() }
    }

    fun invalidate(modelId: String) {
        synchronized(templateCache) { templateCache.remove(modelId) }
    }
}
