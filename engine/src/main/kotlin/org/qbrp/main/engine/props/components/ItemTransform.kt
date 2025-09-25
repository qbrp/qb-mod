package org.qbrp.main.engine.props.components

import org.qbrp.main.engine.items.components.meta.ItemLocation
import org.qbrp.main.engine.physics.dynamics.dataformats.Quat
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.TransformAdapter
import org.qbrp.main.engine.world.location.Location

interface ItemTransform: Location {
    val quat: Quat
    fun physics(): Transform = TransformAdapter(pos, quat)
}