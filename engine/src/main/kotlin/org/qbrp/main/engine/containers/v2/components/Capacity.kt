package org.qbrp.main.engine.containers.v2.components

import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.physics.model.agent.Agent

typealias AcceptResolver = (ContainerEntry) -> Boolean

sealed class Capacity {
    data class Physical(val agent: Agent, val factor: Float) : Capacity()
    data class FixedMass(val mass: Float) : Capacity()
    data class Rules(val resolver: AcceptResolver)
    object Infinity : Capacity()
}

val Capacity.Physical.volume get() = agent.volume * factor