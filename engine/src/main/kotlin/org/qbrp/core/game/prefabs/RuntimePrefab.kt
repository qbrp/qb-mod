package org.qbrp.core.game.prefabs

import org.qbrp.core.game.model.objects.BaseObject

open class RuntimePrefab(val id: String, val category: String, val components: MutableList<PrefabField> = mutableListOf(), val tags: MutableList<Tag> = mutableListOf()) {

    fun getDefaultTag(): Tag {
        return Tag("default", components)
    }

    fun getTag(name: String): Tag {
        return tags.find { it.id == name }!!
    }

    data class Tag(val id: String, var components: MutableList<PrefabField>) {
        fun put(obj: BaseObject) {
            components.forEach { obj.state.addComponentIfNotExist(it.component()) }
        }
    }
}