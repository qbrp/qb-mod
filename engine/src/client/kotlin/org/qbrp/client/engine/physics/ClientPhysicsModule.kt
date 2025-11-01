package org.qbrp.client.engine.physics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.getClientPlayer
import org.qbrp.client.core.onClientTick
import org.qbrp.client.engine.chat.addons.Channels
import org.qbrp.client.engine.players.ClientMinecraftEntities
import org.qbrp.client.engine.players.ClientPlayerList
import org.qbrp.client.engine.players.ClientPlayerStorage
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.entity.PhysicsVec3
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.PhysicsSimulation
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.physics.system.service.createWorldGround
import org.qbrp.main.engine.physics.chunk.ChunkBank
import org.qbrp.main.engine.physics.chunk.PhysicsChunkManager
import org.qbrp.main.engine.physics.player.JoltPlayerBodyManager
import org.qbrp.main.engine.physics.player.PlayerBodyManager
import org.qbrp.main.engine.physics.player.PlayerPhysicsSystem
import org.qbrp.main.engine.physics.player.playerCharacter
import org.qbrp.main.engine.players.model.PlayerDisconnectEvent
import org.qbrp.main.engine.players.model.ServerPlayerObject
import kotlin.math.min

@Autoload(env = EnvType.CLIENT)
class ClientPhysicsModule : EngineModule("client-physics") {
    private val physics: PhysicsSimulation by inject()

    override fun getKoinModule(): Module = module {
        single<ChunkBank> { ChunkBank(get()) }
        single<PhysicsChunkManager> { getLocal<ChunkBank>() }
        single { PlayerPhysicsSystem() }
        single<PlayerBodyManager> { JoltPlayerBodyManager(get(), get(), get<ClientPlayerStorage>()) }
    }

    override fun onEnable() {
        val playerPhysicsSystem: PlayerPhysicsSystem by inject()
        val clientPlayerStorage: ClientPlayerList by inject()
        val clientMinecraftEntities: ClientMinecraftEntities by inject()

        var lastJob: Job? = null
        var ticks = 0f

        ClientTickEvents.START_WORLD_TICK.register {
            ticks = min(ticks + TICK_RATE, MAX_TICK_RATE)
            playerPhysicsSystem.beforeSimulation(clientPlayerStorage, clientMinecraftEntities)

            if (lastJob?.isCompleted != false) {
                lastJob = physics.step(ticks)
                ticks = 0f
            }
        }

        on<PlayerDisconnectEvent>() {
            lastJob?.cancel()
            lastJob = null
        }
    }

    companion object {
        const val TICK_RATE = 0.05f
        const val MAX_TICK_RATE = 0.1f
    }
}