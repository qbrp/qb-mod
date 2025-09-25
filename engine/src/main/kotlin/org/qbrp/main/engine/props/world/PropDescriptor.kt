package org.qbrp.main.engine.props.world

import org.qbrp.main.engine.physics.dynamics.dataformats.RigidBodyDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.model.VolumetricShape

class PropDescriptor(
    override val shape: VolumetricShape,
    override val transform: Transform,
    override val mass: Double
) : RigidBodyDescriptor