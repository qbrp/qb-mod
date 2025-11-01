package org.qbrp.main.engine.physics.system.service

import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.entity.Transform

interface WorldBodyManager {
    val bodies: List<Body>
    val simulation: PhysicsSimulation

    fun addBody(body: Body, activate: Boolean, broadcastEvent: Boolean = true, callback: () -> Unit = {})
    fun destroyBody(body: Body, broadcastEvent: Boolean = true)
    fun destroyBody(id: BodyId, broadcastEvent: Boolean = true)
    fun addCharacter(character: PhysicalCharacter)
    fun removeCharacter(character: PhysicalCharacter)
    fun activate(body: Body)
    fun nearest(pos: Pos, distance: Double): List<Body> {
        return bodies.filter { it.transform.pos.asVec3().squaredDistanceTo(pos.asVec3()) < distance * distance }
    }
    fun setTransform(id: BodyId, transform: Transform)
    fun raycast(start: Pos, end: Pos): List<Body>
    fun destroyAll() {
        bodies.forEach { destroyBody(it) }
    }
    fun removeCharacters()
    fun invalidate() {
        removeCharacters()
        destroyAll()
    }

    fun getBody(id: BodyId): Body?
}