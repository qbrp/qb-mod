package org.qbrp.core.game.model.objects

import org.qbrp.core.game.IDGenerator
import org.qbrp.core.game.lifecycle.Lifecycle
import org.qbrp.core.game.model.State
import org.qbrp.core.game.model.Stateful
import org.qbrp.core.game.serialization.ObjectJsonField

abstract class BaseObject(
    val name: String,
    open val id: Long = IDGenerator.nextId(),
    open override val state: State = State(),
    eternal: Boolean = false,
    ephemeral: Boolean = false,
    open val lifecycle: Lifecycle<BaseObject>
) : Stateful {
    open var eternal: Boolean = eternal
        protected set
    open var ephemeral: Boolean = ephemeral
        protected set

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: BaseObject> getTypedLifecycle(): Lifecycle<T> {
        return lifecycle as Lifecycle<T>
    }

    open fun unload() {
        lifecycle.unload(this)
    }

    open fun save() {
        lifecycle.save(this)
    }

    open fun tick() = Unit

    inline fun <reified T> getComponent(): T? {
        return state.getComponent<T>()
    }

    open fun getJsonField(): ObjectJsonField = ObjectJsonField(id, name, state, eternal)
}