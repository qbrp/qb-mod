package org.qbrp.core.game.model.components

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.game.model.State
import org.qbrp.core.game.prefabs.Prefab.PrefabField

abstract class Component(): PrefabField {
    @JsonIgnore protected var state: State? = null
    @JsonIgnore open val save = true
    fun requireState(): State {
        return state ?: throw NullPointerException("Компонент $this не помещен в какое-либо состояние, и оно не может быть передано")
    }
    fun putState(state: State){
        this.state = state
    }

    override fun component(): Component {
        return this
    }

    inline fun <reified T> getComponent(): T? = requireState().getComponent<T>()
    inline fun <reified T> getComponentOrThrow(): T = requireState().getComponentOrThrow<T>()
    fun getComponentByName(name: String): Component = requireState().getComponentByNameOrThrow(name)

}
