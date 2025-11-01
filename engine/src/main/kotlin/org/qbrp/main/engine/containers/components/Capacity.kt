package org.qbrp.main.engine.containers.components

import org.qbrp.main.engine.chemistry.agent.Agent

sealed class Capacity {
    data class Physical(val agent: Agent, val factor: Float) : Capacity()
    data class FixedMass(val mass: Float) : Capacity()
    object Infinity : Capacity()
}

val Capacity.Physical.volume get() = agent.volume * factor