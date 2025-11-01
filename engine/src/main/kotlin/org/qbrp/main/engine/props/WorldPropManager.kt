package org.qbrp.main.engine.props

import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.props.components.SpawnedProp

interface WorldPropManager {
    val props: Collection<SpawnedProp>
    fun getProp(bodyId: BodyId): SpawnedProp? {
        return props.find { it.body.id == bodyId }
    }
    fun getProp(body: Body): SpawnedProp? {
        return props.find { it.body.id == body.id }
    }
}