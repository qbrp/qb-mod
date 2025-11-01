package org.qbrp.main.network.physics

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.chunk.PhysicsChunkPos
import org.qbrp.main.network.transport.readBodyId
import org.qbrp.main.network.transport.readEnum
import org.qbrp.main.network.transport.readProtobuf
import org.qbrp.main.network.transport.readTransform
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint
import org.qbrp.main.network.transport.writeBodyId
import org.qbrp.main.network.transport.writeProtobuf
import org.qbrp.main.network.transport.writeTransform

val PHYSICS_DESTROY_BODY_ENDPOINT = NetworkChannel<PhysicsDestroyBodyPacket>().wrapEndpoint()

@Serializable
data class PhysicsDestroyBodyPacket(val id: BodyId) : Packet

val PHYSICS_BODY_ENDPOINT = NetworkChannel<PhysicsBodyPacket>(
    codec = PacketCodec.Binary<PhysicsBodyPacket>(
        {
            PhysicsBodyPacket(
                readCollection(
                    { mutableListOf<BodyData>() },
                    { buf ->
                        BodyData(
                            buf.readBodyId(),
                            buf.readTransform(),
                            buf.readProtobuf(ShapeDto.serializer()).toEntity(),
                            buf.readDouble(),
                            buf.readEnum()
                        )
                    }
                )
            )
        },
        { p ->
            writeCollection(p.bodies) { buf, elem ->
                buf.writeBodyId(elem.id)
                buf.writeTransform(elem.transform)
                buf.writeProtobuf(elem.shape.toDto(), ShapeDto.serializer())
                buf.writeDouble(elem.mass)
                buf.writeEnumConstant(elem.type)
            }
        }
    )
)
    .wrapEndpoint()

data class PhysicsBodyPacket(val bodies: List<BodyData>) : Packet

val PHYSICS_TRANSFORM_ENDPOINT = NetworkChannel<PhysicsTransformPacket>(
    codec = PacketCodec.Binary<PhysicsTransformPacket>(
        {
            PhysicsTransformPacket(
                readCollection(
                    { mutableListOf<BodyTransform>() },
                    { buf -> BodyTransform(buf.readBodyId(), buf.readTransform()) }
                )
            )
        },
        { p ->
            writeCollection(p.transforms) { buf, elem ->
                buf.writeBodyId(elem.id)
                buf.writeTransform(elem.transform)
            }
        }
    )
)
    .wrapEndpoint()

data class PhysicsTransformPacket(val transforms: List<BodyTransform>) : Packet

val PHYSICS_CHUNK_ENDPOINT = NetworkChannel<PhysicsChunkPacket>(
    codec = PacketCodec.Binary<PhysicsChunkPacket>(
        {
            PhysicsChunkPacket(
                ChunkData(
                    readInt(),
                    readInt(),
                    readNullable({ buf -> readShape() as Shape.SegmentCompound }),
                    PhysicsChunkPos(readInt(), readInt())
                )
            )
        },
        { p ->
            val chunk = p.chunk
            writeInt(chunk.minY)
            writeInt(chunk.maxY)
            writeNullable(chunk.colliderShape) { buf, elem -> buf.writeShape(elem) }
            writeInt(chunk.pos.x)
            writeInt(chunk.pos.z)
        }
    )
)
    .wrapEndpoint()

data class PhysicsChunkPacket(
    val chunk: ChunkData,
) : Packet

data class ChunkData(
    val minY: Int,
    val maxY: Int,
    val colliderShape: Shape.SegmentCompound?,
    val pos: PhysicsChunkPos
)