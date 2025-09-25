package org.qbrp.main.engine.physics.dynamics.simulation

import com.github.stephengold.joltjni.BodyCreationSettings
import com.github.stephengold.joltjni.PhysicsSystem
import com.github.stephengold.joltjni.Plane
import com.github.stephengold.joltjni.PlaneShape
import com.github.stephengold.joltjni.Vec3
import com.github.stephengold.joltjni.enumerate.EMotionType
import com.github.stephengold.joltjni.enumerate.EOverrideMassProperties
import org.qbrp.main.engine.physics.dynamics.CollisionLayers
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.physics.dynamics.dataformats.BodyDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.Descriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.LayerDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.PlaneDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.RigidBodyDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.jolt
import org.qbrp.main.engine.physics.dynamics.dataformats.joltR

class JoltBodyFactory(private val system: PhysicsSystem, private val shapeFactory: JoltShapeFactory): BodyFactory {
    override fun build(descriptor: Descriptor): Body {
        val settings = BodyCreationSettings()
        settings.setMotionType(EMotionType.Static)
        //Primary params
        when (descriptor) {
            is BodyDescriptor -> defaultBodyParams(descriptor, settings)
            is PlaneDescriptor -> planeBodyParams(descriptor, settings)
        }
        //Additional
        when (descriptor) {
            is RigidBodyDescriptor -> rigidBodyAdditionalParams(descriptor, settings)
        }
        if (descriptor is LayerDescriptor) {
            settings.setObjectLayer(descriptor.layer)
        } else {
            settings.setObjectLayer(CollisionLayers.PROP)
        }
        val body = system.bodyInterface.createBody(settings)
        return LoadedJoltBody(body.id, descriptor.mass, body)
    }

    private fun defaultBodyParams(descriptor: BodyDescriptor, settings: BodyCreationSettings) {
        settings.setShape(shapeFactory.create(descriptor.shape))
        settings.setPosition(descriptor.transform.pos.joltR())
        settings.setRotation(descriptor.transform.quat.jolt())
        settings.setAllowSleeping(descriptor.allowSleep)
    }

    private fun planeBodyParams(descriptor: PlaneDescriptor, settings: BodyCreationSettings) {
        val groundY = descriptor.y
        val normal = Vec3.sAxisY()
        val plane = Plane(normal, -groundY.toFloat())
        settings.apply {
            motionType = EMotionType.Static
            objectLayer = CollisionLayers.PROP
            shape = PlaneShape(plane)
        }
    }

    private fun rigidBodyAdditionalParams(descriptor: RigidBodyDescriptor, settings: BodyCreationSettings) {
        settings.massPropertiesOverride.setMass(descriptor.mass.toFloat())
        settings.setOverrideMassProperties(EOverrideMassProperties.CalculateInertia)
        settings.setMotionType(EMotionType.Dynamic)
    }
}