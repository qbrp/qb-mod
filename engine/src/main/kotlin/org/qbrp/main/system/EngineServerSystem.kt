package org.qbrp.main.system

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.fabricmc.MinecraftAdapterSystem
import org.qbrp.main.core.fabricmc.WorldSystem
import org.qbrp.main.core.fabricmc.ActionBarSystem
import org.qbrp.main.engine.containers.system.ContainerSystem
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.system.InteractionSystem
import org.qbrp.main.engine.controls.system.modes.InteractionModeSystem
import org.qbrp.main.engine.physics.player.MinecraftEntities
import org.qbrp.main.engine.physics.player.PlayerMovementSystem
import org.qbrp.main.engine.physics.player.PlayerPhysicsSystem
import org.qbrp.main.engine.physics.player.PlayerTeleportationSystem
import org.qbrp.main.engine.physics.player.ServerMinecraftEntities
import org.qbrp.main.engine.physics.system.service.ManipulatorSystem
import org.qbrp.main.engine.physics.system.service.PhysicsSimulation
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import org.qbrp.main.engine.spectators.SpectatingSystem
import org.qbrp.main.engine.world.model.MainWorld
import org.qbrp.main.network.NetworkSystems
import org.qbrp.main.network.ServerConnectionManager

class EngineServerSystem(
    private val mainWorld: MainWorld,
    private val playerList: ServerPlayerList,
    private val minecraftAdapterSystem: MinecraftAdapterSystem,
    private val minecraftEntities: ServerMinecraftEntities,
    private val worldSystem: WorldSystem,
    private val actionBarSystem: ActionBarSystem,
    private val networkSystems: NetworkSystems,
    private val connectionManager: ServerConnectionManager,
    private val containerSystem: ContainerSystem,
    private val interactionSystem: InteractionSystem,
    private val physicsSimulation: PhysicsSimulation,
    private val chunkLoadSchedulerSystem: ChunkLoadSchedulerSystem,
    private val spectatingSystem: SpectatingSystem,
    private val movementSystem: PlayerMovementSystem,
    private val manipulatorSystem: ManipulatorSystem,
    private val playerPhysicsSystem: PlayerPhysicsSystem,
    private val playerTeleportationSystem: PlayerTeleportationSystem,
    private val interactionModeSystem: InteractionModeSystem
) {
    private var ticks = 0
    fun updateSystems() {
        ticks++

        playerPhysicsSystem.beforeSimulation(playerList, minecraftEntities)
        val physicsJob = physicsSimulation.step {
            manipulatorSystem.onStep()
        }

        actionBarSystem.update(playerList, minecraftEntities)

        minecraftAdapterSystem.update()
        spectatingSystem.update(playerList)
        worldSystem.update(mainWorld)
        containerSystem.update()
        interactionSystem.update()
        interactionModeSystem.update(playerList, minecraftEntities)

        movementSystem.update(playerList)
        playerTeleportationSystem.update(playerList)
        chunkLoadSchedulerSystem.tickTimer()

        networkSystems.tickNetworkSystems(playerList)
        runBlocking { physicsJob.join() }
    }

    fun connect(entity: ServerPlayerEntity) {
        connectionManager.onConnect(entity)
    }

    fun disconnect(entity: ServerPlayerEntity) {
        connectionManager.onDisconnect(entity)
    }
}