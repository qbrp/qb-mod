package org.qbrp.main.network.physics

import kotlinx.serialization.Serializable
import net.minecraft.network.PacketByteBuf
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.Flat
import org.qbrp.main.engine.physics.system.entity.Segment
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.network.transport.readProtobuf
import org.qbrp.main.network.transport.writeProtobuf

enum class BodyType {
    CHUNK, OTHER
}

data class BodyData(
    val id: BodyId,
    val transform: Transform,
    val shape: Shape,
    val mass: Double,
    val type: BodyType
)

data class BodyTransform(
    val id: BodyId,
    val transform: Transform
)

@Serializable
data class SegmentDto(val y: Int, val flats: List<FlatDto>, val shapes: List<ShapeDto.Box>)

@Serializable
data class FlatDto(val startX: Int, val endX: Int, val startZ: Int, val endZ: Int)

@Serializable
sealed class ShapeDto {
    @Serializable
    data class Box(val xHalf: Double, val yHalf: Double, val zHalf: Double) : ShapeDto()
    @Serializable
    data class SegmentsCompound(val segments: List<SegmentDto>) : ShapeDto()
    @Serializable
    data class Plane(val y: Double) : ShapeDto()
}

fun PacketByteBuf.writeShape(shape: Shape) {
    writeProtobuf(shape.toDto(), ShapeDto.serializer())
}

fun PacketByteBuf.readShape(): Shape {
    return readProtobuf(ShapeDto.serializer()).toEntity()
}

fun Shape.Box.toDto() = ShapeDto.Box(xHalf, yHalf, zHalf)

fun Shape.toDto() = when (this) {
    is Shape.Box -> toDto()
    is Shape.SegmentCompound -> ShapeDto.SegmentsCompound(segments.map { it.toDto() })
    is Shape.Plane -> ShapeDto.Plane(y)
}

fun Flat.toDto() = FlatDto(startX, endX, startZ, endZ)

fun Segment.toDto() = SegmentDto(y, flats.map { it.toDto() }, shapes.map { it.toDto() })

fun ShapeDto.Box.toEntity() = Shape.Box(xHalf, yHalf, zHalf)

fun ShapeDto.toEntity(): Shape {
    return when (this) {
        is ShapeDto.Box -> toEntity()
        is ShapeDto.Plane -> Shape.Plane(y)
        is ShapeDto.SegmentsCompound -> Shape.SegmentCompound(
            segments.map {
                Segment(
                    it.y,
                    it.flats.map { Flat(it.startX, it.endX, it.startZ, it.endZ) },
                    it.shapes.map { it.toEntity() }
                )
            }
        )
    }
}