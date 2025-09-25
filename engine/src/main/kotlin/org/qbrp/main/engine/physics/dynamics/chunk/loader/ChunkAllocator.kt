package org.qbrp.main.engine.physics.dynamics.chunk.loader

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import org.qbrp.main.engine.physics.dynamics.ChunkConstants
import org.qbrp.main.engine.physics.dynamics.CollisionLayers
import org.qbrp.main.engine.physics.dynamics.chunk.factory.ChunkColliderFactory
import org.qbrp.main.engine.physics.dynamics.chunk.factory.LoadSettings
import org.qbrp.main.engine.physics.dynamics.chunk.factory.particleBlocks
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.physics.dynamics.dataformats.BodyDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.LayerDescriptor
import org.qbrp.main.engine.physics.dynamics.dataformats.Shape
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.dynamics.simulation.BodyFactory
import org.qbrp.main.engine.physics.dynamics.simulation.WorldBodyManager

class ChunkAllocator(private val world: WorldBodyManager,
                     private val colliderFactory: ChunkColliderFactory,
                     private val bodyFactory: BodyFactory
): PhysicsChunkManager, ChunkLoadDebugger {
    override var enabled: Boolean = false

    class ChunkDescriptor(
        override val shape: Shape,
        override val transform: Transform,
        override val layer: Int = CollisionLayers.WORLD
    ): BodyDescriptor, LayerDescriptor
    private val chunks: MutableMap<ChunkPos, Body> = mutableMapOf()

    override fun load(chunk: Chunk, serverWorld: ServerWorld, settings: LoadSettings) {
        unload(chunk, serverWorld)
        val collider = colliderFactory.build(serverWorld, chunk, settings)
        if (enabled) collider.particleBlocks(serverWorld, chunk.pos)
        if (collider.isEmpty()) return
        val chunkTransform = Vec3Adapter(chunk.pos.startX, 0, chunk.pos.startZ)
        val chunkBody = bodyFactory.build(ChunkDescriptor(collider, chunkTransform.toTransform()))
        chunks[chunk.pos] = chunkBody
        world.addBody(chunkBody, true, false)
    }

    override fun unload(chunk: Chunk, serverWorld: ServerWorld) {
        chunks.remove(chunk.pos)?.let { world.destroyBody(it, false) }
    }
}