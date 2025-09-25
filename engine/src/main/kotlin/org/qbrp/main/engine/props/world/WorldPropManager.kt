package org.qbrp.main.engine.props.world

import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.props.components.SpawnedProp

interface WorldPropManager {
    val props: Collection<SpawnedProp>
    fun getProp(bodyId: Int): SpawnedProp? {
        return props.find { it.body.id == bodyId }
    }
    fun getProp(body: Body): SpawnedProp? {
        return props.find { it.body.id == body.id }
    }
}