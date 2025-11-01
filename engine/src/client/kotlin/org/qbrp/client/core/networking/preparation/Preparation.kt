package org.qbrp.client.core.networking.preparation

import org.koin.core.Koin
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.main.core.util.math.getChunkPos
import org.qbrp.main.engine.physics.chunk.PhysicsChunkManager
import org.qbrp.main.engine.physics.chunk.asPhysics
import org.qbrp.main.engine.players.model.pos

fun inEngineLoaded(koinContext: Koin): Boolean {
    val clientPlayerManager: ClientPlayerManager by koinContext.inject()
    val chunkManager: PhysicsChunkManager by koinContext.inject()

    val player = clientPlayerManager.clientPlayer
    val playerChunkPos = player?.pos?.getChunkPos()?.asPhysics()
    val chunksLoaded = playerChunkPos?.let { chunkManager.chunkLoadedAt(it) } ?: false

    return clientPlayerManager.isInstantiated && chunksLoaded
}