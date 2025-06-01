package org.qbrp.main.core.game.model.objects

import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.model.Stateful

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

    inline fun <reified T> getComponent(): T? {
        return state.getComponent<T>()
    }

    protected fun <T> tickState(ctx: T) = state.tick<T>(ctx)
}