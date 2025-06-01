package org.qbrp.main.core.game.prefabs

import org.qbrp.main.core.game.model.objects.BaseObject

//TODO: Убрать и заменить на обычный Prefab
//Этот класс - пережиток прошлого, предназначен для "инициализирующихся во время загрузки мода" префабов, например, игрока
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