package org.qbrp.main.network.physics

import net.minecraft.util.math.ChunkPos
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.core.util.math.getChunkPos
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.chunk.PhysicsChunk
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.network.transport.PacketSendTask
import kotlin.math.ceil
import kotlin.math.min


fun chunkSquare(from: ChunkPos, size: Int): Array<ChunkPos> {
    return Array<ChunkPos>(size * size) { i ->
        val z = i / size
        val x = i % size
        ChunkPos(from.x + x, from.z + z)
    }
}

fun nearestPlayerChunks(p: PlayerObject) = chunkSquare(p.pos.getChunkPos(), 3)

fun PlayerReference.taskPhysicsChunkPacket(chunk: PhysicsChunk): PacketSendTask {
    return PHYSICS_CHUNK_ENDPOINT
        .taskS2C(
            PhysicsChunkPacket(
                ChunkData(
                    chunk.minY,
                    chunk.maxY,
                    chunk.collider.shape as Shape.SegmentCompound?,
                    chunk.pos
                )
            ),
            this
        )
}

internal fun List<Body>.flattenBodiesToSnapshots(): List<BodySnapshot> =
    map { b -> BodySnapshot(b.id, b.transform, b.shape, b.mass) }

internal fun nearestBodiesToPlayersSnapshot(
    bodies: List<BodySnapshot>,
    players: List<ServerPlayerObject>,
    distance: Double = 42.0
): Map<ServerPlayerObject, List<BodySnapshot>> {
    val r2 = distance * distance
    return players.associateWith { player ->
        bodies.filter {
            val distance = it.transform.pos.asVec3().squaredDistanceTo(player.pos.asVec3())
            distance < r2
        }
    }
}

internal fun Map<ServerPlayerObject, List<BodySnapshot>>.filterBodiesSnapshot(
    criteria: (BodySnapshot) -> Boolean
) = mapValues { (_, list) -> list.filter { criteria(it) } }

internal fun Map<ServerPlayerObject, List<BodySnapshot>>.mapToBodyDataSnapshot(
    typeCriteria: (BodySnapshot) -> BodyType
) = mapValues { (_, list) ->
    list.map { s -> BodyData(s.id, s.transform, s.shape, s.mass, typeCriteria(s)) }
}

internal fun Map<ServerPlayerObject, List<BodySnapshot>>.mapToBodyTransformsSnapshot() =
    mapValues { (_, list) ->
        list.map { s -> BodyTransform(s.id, s.transform) }
    }

/* ---------- packets ---------- */

fun Map<ServerPlayerObject, List<BodyData>>.sendDataPackets() {
    forEach { player, bodies ->
        if (bodies.isNotEmpty()) {
            PHYSICS_BODY_ENDPOINT.sendS2C(PhysicsBodyPacket(bodies), player)
        }
    }
}

fun Map<ServerPlayerObject, List<BodyTransform>>.sendTransformPackets() {
    forEach { player, bodies ->
        if (bodies.isNotEmpty()) {
            PHYSICS_TRANSFORM_ENDPOINT.sendS2C(PhysicsTransformPacket(bodies), player)
        }
    }
}

fun List<PlayerReference>.broadcastBodyDestroyPacket(id: BodyId) {
    PHYSICS_DESTROY_BODY_ENDPOINT
        .broadcast(PhysicsDestroyBodyPacket(id), this)
}
