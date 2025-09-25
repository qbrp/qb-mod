package org.qbrp.main.core.assets.prefabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.components.construct.ComponentBuilderOperator
import org.qbrp.main.core.components.serialization.ComponentJsonField
import org.qbrp.main.engine.items.PrefabEntryKey

@Serializable
open class Prefab(@SerialName("id") val identifier: String, val components: List<ComponentJsonField>, val tags: List<Tag>): Asset() {
    fun mergeTagWith(op: ComponentBuilderOperator<*>, key: PrefabEntryKey) {
        tags.find { it.id == key.tag }?.mergeAndPut(op)
    }

    fun getTag(key: PrefabEntryKey): Tag {
        return tags.find { it.id == key.tag }!!
    }

    @Serializable
    class Tag(
        val id: String,
        val components: List<ComponentJsonField>,
        @Transient var parentPrefab: Prefab? = null
    ): KoinComponent {
        fun mergedWith() = parentPrefab!!.components + components

        fun mergeAndPut(op: ComponentBuilderOperator<*>) {
            mergedWith().forEach {
                op.addComponentIfNotExist(it.type, { it.toComponent(op, get()) })
            }
        }
    }

    init {
        tags.forEach { tag ->
            tag.parentPrefab = this
        }
    }
}