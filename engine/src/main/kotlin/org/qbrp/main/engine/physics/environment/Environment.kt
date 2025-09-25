package org.qbrp.main.engine.physics.environment

import net.minecraft.util.math.Vec3d
import org.qbrp.main.engine.physics.model.agent.Agent

interface Environment: Atmosphere {
    var temperature: Double
    val gravity: Double
    var pressure: Double
    fun createAgent(agent: Agent, pos: Vec3d)
    fun launch(from: Agent, agent: Agent, direction: Vec3d, impulse: Double)
    fun detach(from: Agent, agent: Agent)
}