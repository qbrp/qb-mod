package org.qbrp.main.engine.physics.player

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.addOrModify
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.replace
import org.qbrp.main.core.components.extensions.replaceOrAdd
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.ZeroVec3
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.entity.toShape
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.PhysicsSimulation
import org.qbrp.main.engine.physics.system.service.assertIsEditable
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorldVec3

fun BodyFactory.playerCharacter(pos: Pos): PhysicalCharacter {
    return character(Dimensions(0.6, 0.4, 1.7).toShape(), 50.0, pos)
}

// Character

class PlayerPhysicalCharacter(
    private val _character: PhysicalCharacter,
    private val simulation: PhysicsSimulation,
) : DataComponent {
    val character: PhysicalCharacter
        get() {
            return _character
        }
}

val PlayerObject.character
    get() = get<PlayerPhysicalCharacter>()?.character

// Velocity

data class VelocityComponent(var vec3: WorldVec3, var dirty: Boolean = true) : DataComponent

val PlayerObject.velocity
    get() = get<VelocityComponent>()?.vec3 ?: ZeroVec3().asWorldVec3()

fun PlayerObject.setVelocity(to: WorldVec3) {
    getOrAdd { VelocityComponent(velocity) }.apply {
        this.vec3 = to
        this.dirty = true
    }
}

// Update Position

class NewPositionComponent(val pos: WorldPos) : DataComponent

fun PlayerObject.setPosition(to: WorldPos) {
    replaceOrAdd { NewPositionComponent(to) }
}

// Is Supported

data class IsSupportedComponent(var isSupported: Boolean) : DataComponent

val PlayerObject.isSupported get() = this.get<IsSupportedComponent>()?.isSupported == true