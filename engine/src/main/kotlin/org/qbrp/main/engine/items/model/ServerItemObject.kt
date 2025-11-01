package org.qbrp.main.engine.items.model

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.util.IDGenerator
import org.qbrp.main.engine.containers.entity.PhysicalEntry
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.environment.Environment

open class ServerItemObject(
    override val id: String,
    override val state: ItemState,
) : ItemObject(state), PhysicalEntry {
    constructor(
        ctx: ItemInstantiateContext,
        state: ItemState = ItemStateImpl(),
        id: String = IDGenerator.nextId().toString()
    ): this(
        id,
        state
    )

    override val agent: Agent? get() = get<Agent>()

    override fun tickPhysics(
        env: Environment,
        parentEnvironment: Environment?
    ) {
        agent?.tickPhysics(env, parentEnvironment)
    }
}
