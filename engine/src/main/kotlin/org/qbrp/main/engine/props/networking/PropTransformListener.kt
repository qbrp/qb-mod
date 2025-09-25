package org.qbrp.main.engine.props.networking

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.TransformAdapter

interface PropTransformListener: PacketListener {
    fun onTransform(id: String, transform: TransformAdapter)
}