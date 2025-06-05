package org.qbrp.main.core.game.model.objects

import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.model.Stateful

abstract class BaseObject(
    override val id: String = IDGenerator.nextId().toString(),
    override val state: State = State(),
    eternal: Boolean = false,
    ephemeral: Boolean = false
) : BaseEntity<BaseObject>(id), Stateful {
    open var eternal: Boolean = eternal
        protected set
    open var ephemeral: Boolean = ephemeral
        protected set

    inline fun <reified T> getComponent(): T? {
        return state.getComponent<T>()
    }

    protected fun <T> tickState(ctx: T) = state.tick<T>(ctx)
}