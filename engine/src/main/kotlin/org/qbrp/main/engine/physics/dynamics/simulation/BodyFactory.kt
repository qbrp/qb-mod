package org.qbrp.main.engine.physics.dynamics.simulation

import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.physics.dynamics.dataformats.BodyDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.Descriptor

interface BodyFactory {
    fun build(descriptor: Descriptor): Body
}