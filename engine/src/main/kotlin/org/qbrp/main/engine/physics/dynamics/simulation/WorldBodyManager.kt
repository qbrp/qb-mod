package org.qbrp.main.engine.physics.dynamics.simulation

import com.github.stephengold.joltjni.RayCast
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter

interface WorldBodyManager {
    val bodies: List<Body>
    fun addBody(body: Body, activate: Boolean, broadcastEvent: Boolean = true)
    fun destroyBody(body: Body, broadcastEvent: Boolean = true)
    fun activate(body: Body)
    fun nearest(pos: Position, distance: Double): List<Body> {
        return bodies.filter { it.transform.pos.squaredDistanceTo(pos) < distance * distance }
    }
    fun raycast(start: Position, end: Position): List<Body>
}