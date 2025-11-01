package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.BodyCreationSettings
import com.github.stephengold.joltjni.Character
import com.github.stephengold.joltjni.CharacterSettings
import com.github.stephengold.joltjni.PhysicsSystem
import com.github.stephengold.joltjni.Plane
import com.github.stephengold.joltjni.PlaneShape
import com.github.stephengold.joltjni.Quat
import com.github.stephengold.joltjni.Vec3
import com.github.stephengold.joltjni.enumerate.EMotionType
import com.github.stephengold.joltjni.enumerate.EOverrideMassProperties
import org.litote.kmongo.hour
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.CollisionLayers
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.BodyDescriptor
import org.qbrp.main.engine.physics.system.entity.MutableCompoundBody
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.spongepowered.asm.mixin.Mutable

class JoltBodyFactory(private val system: PhysicsSystem) : BodyFactory {
    override fun build(descriptor: BodyDescriptor): Body {
        val settings = BodyCreationSettings()
        settings.setMotionType(EMotionType.Static)

        settings.setShape(createJoltShape(descriptor.shape))
        settings.setPosition(descriptor.transform.pos.asVec3().joltR())
        settings.setRotation(descriptor.transform.rot.jolt())
        settings.setAllowSleeping(descriptor.allowSleep)

        var isStatic = true
        if (descriptor.mass > 0.0) {
            settings.massPropertiesOverride.setMass(descriptor.mass.toFloat())
            settings.setOverrideMassProperties(EOverrideMassProperties.CalculateInertia)
            settings.setMotionType(EMotionType.Dynamic)
            isStatic = false
        }

        settings.setObjectLayer(descriptor.layer)
        val body = system.bodyInterface.createBody(settings)
        return JoltBody(body.id, descriptor.mass, descriptor.shape, system, body, isStatic)
    }

    override fun plane(y: Int): Body {
        val normal = Vec3.sAxisY()
        val plane = Plane(normal, -y.toFloat())
        val settings = BodyCreationSettings()
        settings.apply {
            setMotionType(EMotionType.Static)
            setObjectLayer(CollisionLayers.PROP_DYNAMIC)
            setShape(PlaneShape(plane))
        }
        val body = system.bodyInterface.createBody(settings)
        return JoltBody(body.id, 0.0, Shape.Plane(y.toDouble()), system, body, true)
    }

    override fun character(shape: Shape, mass: Double, location: Pos): PhysicalCharacter {
        val joltShape = createJoltShape(shape)
        val settings = CharacterSettings()
        settings.shape = joltShape
        settings.layer = CollisionLayers.PLAYER_DYNAMIC
        return JoltCharacter(Character(settings, location.asVec3().joltR(), Quat(), 0L, system))
    }

    override fun mutableCompoundBody(shapes: Shape.Multi, transform: Transform): MutableCompoundBody {
        val joltShape = joltMutableMultiShape(shapes)
        val settings = BodyCreationSettings()
        settings.apply {
            setMotionType(EMotionType.Static)
            setObjectLayer(CollisionLayers.WORLD_STATIC)
            setShape(joltShape)
        }
        val body = system.bodyInterface.createBody(settings)
        val joltBody = JoltBody(body.id, 0.0, shapes, system, body, true)
        return JoltMutableCompoundBody(joltBody, joltShape)
    }
}
