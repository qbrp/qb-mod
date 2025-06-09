package org.qbrp.main.core.game.model.components

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.model.StateEntry
import org.qbrp.main.core.game.prefabs.PrefabField

@Serializable
abstract class Component(): PrefabField, StateEntry {
    @Transient override var state: State? = null
    @Transient open val save = true
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
