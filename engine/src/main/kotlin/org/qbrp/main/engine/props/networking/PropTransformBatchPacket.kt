package org.qbrp.main.engine.props.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.readTransform
import org.qbrp.main.core.networking.writeTransform
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.TransformAdapter

class PropTransformBatchPacket(val transforms: Map<String, Transform>): Packet<PropTransformListener> {
    constructor(buf: PacketByteBuf): this(
        buf.readMap<String, Transform>({ buf -> buf.readString() }, { buf -> buf.readTransform() })
    )

    override fun write(buf: PacketByteBuf) {
        buf.writeMap(
            transforms,
            { buf, key -> buf.writeString(key) },
            { buf, value -> buf.writeTransform(value) })
    }

    override fun apply(listener: PropTransformListener) {
        transforms.forEach { key, transform ->
            listener.onTransform(key, TransformAdapter(transform.pos, transform.quat))
        }
    }
}