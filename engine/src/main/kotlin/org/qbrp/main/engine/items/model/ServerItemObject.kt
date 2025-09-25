package org.qbrp.main.engine.items.model

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.engine.containers.v2.entity.PhysicalEntry
import org.qbrp.main.engine.physics.model.agent.Agent
import org.qbrp.main.engine.physics.environment.Environment
import org.qbrp.main.engine.controls.model.item.ItemInteractable

open class ServerItemObject(
    override val id: String,
    override val state: ItemState,
) : ItemObject(state), PhysicalEntry {
    constructor(ctx: ItemInstantiateContext, state: ItemState = ItemStateImpl(), id: String = IDGenerator.nextId().toString()): this(
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
