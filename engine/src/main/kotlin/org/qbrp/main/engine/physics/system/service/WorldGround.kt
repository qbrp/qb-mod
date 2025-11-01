package org.qbrp.main.engine.physics.system.service

import org.qbrp.main.engine.physics.chunk.ChunkConstants

fun WorldBodyManager.createWorldGround(factory: BodyFactory, y: Int = ChunkConstants.WORLD_MIN_HEIGHT) {
    val body = factory.plane(y)
    addBody(body, true)
}