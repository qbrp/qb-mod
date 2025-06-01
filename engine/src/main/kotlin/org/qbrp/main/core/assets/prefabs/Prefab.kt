package org.qbrp.main.core.assets.prefabs

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.game.model.Stateful
import org.qbrp.main.core.game.serialization.ComponentJsonField
import org.qbrp.main.engine.items.PrefabEntryKey

@Serializable
open class Prefab(override val id: String, val components: List<ComponentJsonField>, val tags: List<Tag>): Asset() {
    fun mergeTagWith(obj: Stateful, key: PrefabEntryKey) {
        tags.find { it.id == key.tag }?.mergeAndPut(obj)
    }

    fun getTag(key: PrefabEntryKey): Tag {
        return tags.find { it.id == key.tag }!!
    }

    @Serializable
    class Tag(
        val id: String,
        val components: List<ComponentJsonField>,
        @Transient var parentPrefab: Prefab? = null
    ) {
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