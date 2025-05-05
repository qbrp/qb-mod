package org.qbrp.core.assets.prefabs

import org.qbrp.core.assets.common.Asset
import org.qbrp.core.game.model.Stateful
import org.qbrp.core.game.model.components.json.ComponentJsonField
import org.qbrp.engine.items.PrefabEntryKey

open class Prefab(id: String, val components: List<ComponentJsonField>, val tags: List<Tag>): Asset(id) {
    fun mergeTagWith(obj: Stateful, key: PrefabEntryKey) {
        tags.find { it.id == key.tag }?.mergeAndPut(obj)
    }

    fun getTag(key: PrefabEntryKey): Tag {
        return tags.find { it.id == key.tag }!!
    }

    class Tag(
        val id: String,
        val components: List<ComponentJsonField>,
        @Transient var parentPrefab: Prefab? = null
    ) {
        val prefabName get() = parentPrefab?.name ?: "unknown"

        fun mergedWith() = parentPrefab!!.components + components

        fun mergeAndPut(obj: Stateful) {
            mergedWith().forEach {
                obj.state.addComponentIfNotExist(it.toComponent())
            }
        }
    }

    init {
        tags.forEach { tag ->
            tag.parentPrefab = this
        }
    }
}