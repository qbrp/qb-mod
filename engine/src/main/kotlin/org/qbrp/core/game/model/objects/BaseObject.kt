package org.qbrp.core.game.model.objects

import org.qbrp.core.game.IDGenerator
import org.qbrp.core.game.lifecycle.Lifecycle
import org.qbrp.core.game.model.State
import org.qbrp.core.game.model.Stateful
import org.qbrp.core.game.serialization.ObjectJsonField

abstract class BaseObject(
    override val lifecycle: Lifecycle<BaseObject>,
    override val id: Long = IDGenerator.nextId(),
    override val state: State = State(),
    eternal: Boolean = false,
    ephemeral: Boolean = false,
) : BaseEntity<BaseObject>(id, lifecycle), Stateful {
    open var eternal: Boolean = eternal
        protected set
    open var ephemeral: Boolean = ephemeral
        protected set

    open fun tick() = Unit

    inline fun <reified T> getComponent(): T? {
        return state.getComponent<T>()
    }
}