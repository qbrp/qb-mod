package org.qbrp.core.mc.player

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.EngineInitializedEvent
import org.qbrp.core.ServerCore
import org.qbrp.core.mc.player.model.AccountDatabaseService
import org.qbrp.core.mc.player.model.PlayerDatabaseService
import org.qbrp.core.mc.player.model.PlayerLifecycleManager
import org.qbrp.core.mc.player.model.PlayerSerializer
import org.qbrp.core.mc.player.model.PlayerStorage
import org.qbrp.core.mc.player.registration.AccountSyncCommand
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.engine.Engine
import org.qbrp.engine.game.GameAPI
import org.qbrp.engine.players.nicknames.NicknameCommand
import org.qbrp.system.utils.world.getPlayersInRadius

object PlayerManager: ServerModCommand, KoinComponent {
    private lateinit var defaultSpeeds: Map<GameMode, Int>

    val playerStorage = PlayerStorage()
    val playerDatabase = PlayerDatabaseService().apply { connect() }
    val accountDatabase = AccountDatabaseService().apply { connect() }
    val serializer = PlayerSerializer(accountDatabase)
    val lifecycleManager = PlayerLifecycleManager(playerStorage, playerDatabase, serializer)

    val playersList
        get() = playerStorage.getAll()

    init {
        ConfigInitializationCallback.EVENT.register {
            loadDefaultSpeeds()
        }
        EngineInitializedEvent.EVENT.register {
            Engine.getAPI<GameAPI>()!!.addWorldTickTask(playerStorage)
        }
    }

    private fun loadDefaultSpeeds() {
        defaultSpeeds = mapOf(
            GameMode.SURVIVAL to ServerResources.getConfig().players.defaultSurvivalSpeed,
            GameMode.CREATIVE to ServerResources.getConfig().players.defaultCreativeSpeed,
            GameMode.SPECTATOR to ServerResources.getConfig().players.defaultSpectatorSpeed,
            GameMode.ADVENTURE to ServerResources.getConfig().players.defaultCreativeSpeed,
        )
    }

    fun loadCommand() = CommandsRepository.add(this)

    fun getPlayer(name: String) = ServerCore.server.playerManager.getPlayer(name)
    fun getPlayerSession(name: String): PlayerObject? = playerStorage.getByPlayerName(name)
    fun getPlayerSession(player: ServerPlayerEntity): PlayerObject = playerStorage.getByPlayer(player)

    fun getDefaultSpeed(gameMode: GameMode): Int {
        return defaultSpeeds[gameMode]!!
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        PlayerManagerCommand().register(dispatcher)
        NicknameCommand().register(dispatcher)
        AccountSyncCommand().register(dispatcher)
    }

    fun getPlayerLookingAt(player: ServerPlayerEntity, gameMode: GameMode? = null): ServerPlayerEntity? {
        val world = player.world
        val lookDirection = player.rotationVector // Предполагается, что это направление взгляда
        val start = player.eyePos // Начальная точка луча (глаза игрока)
        val end = start.add(lookDirection.multiply(5.0)) // Конечная точка луча (5 блоков)

        val entities = world.server!!.playerManager.playerList.getPlayersInRadius(player, 5.0)
            .filter { if (gameMode != null) (it as? ServerPlayerEntity)?.interactionManager?.gameMode == gameMode else true }

        var closestEntity: ServerPlayerEntity? = null
        var closestDistance = Double.MAX_VALUE

        // Проверяем каждую сущность
        for (entity in entities) {
            // Проверяем пересечение луча с ограничивающей рамкой сущности
            val hitResult = entity.boundingBox.raycast(start, end)
            if (hitResult.isPresent) {
                val hitPos = hitResult.get() // Точка пересечения
                val distance = start.distanceTo(hitPos) // Расстояние до точки пересечения
                if (distance < closestDistance) {
                    closestEntity = entity as ServerPlayerEntity
                    closestDistance = distance
                }
            }
        }

        return closestEntity
    }

    fun getLookDirection(player: PlayerEntity): Vec3d {
        val rotation = player.rotationVector
        return Vec3d(rotation.x, rotation.y, rotation.z)
    }
}