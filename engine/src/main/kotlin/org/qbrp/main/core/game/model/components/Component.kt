package org.qbrp.main.core.game.model.components

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.prefabs.PrefabField
import java.util.UUID

@Serializable
abstract class Component(): PrefabField {
    @Transient protected var state: State? = null
    @Transient open val save = true
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
