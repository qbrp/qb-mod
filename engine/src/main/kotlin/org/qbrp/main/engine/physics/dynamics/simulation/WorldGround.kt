package org.qbrp.main.engine.physics.dynamics.simulation

import org.qbrp.main.engine.physics.dynamics.ChunkConstants
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.physics.dynamics.dataformats.PlaneDescriptor

class WorldGround(override val y: Int = ChunkConstants.WORLD_MIN_HEIGHT) : PlaneDescriptor {
    lateinit var body: Body

    fun create(factory: BodyFactory, bodyManager: WorldBodyManager) {
        val body = factory.build(this)
        bodyManager.addBody(body, true)
        this.body = body
    }
}