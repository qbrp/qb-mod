package org.qbrp.core.game.player

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
import net.minecraft.world.RaycastContext
import org.qbrp.core.ServerCore
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.player.registration.AccountSyncCommand
import org.qbrp.core.game.player.registration.LoginCommand
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.utils.world.getPlayersInRadius

object PlayerManager: ServerModCommand {
    private lateinit var defaultSpeeds: Map<GameMode, Int>

    val players: MutableMap<String, ServerPlayerSession> = mutableMapOf()
    val playersList
        get() = players.values.toList()
    val databaseService = DatabaseService(ServerResources.getConfig().databases.nodeUri, "players").also { it.connect() }

    init {
        ConfigInitializationCallback.EVENT.register {
            loadDefaultSpeeds()
        }
        loadDefaultSpeeds()
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
    fun getPlayerSession(name: String): ServerPlayerSession? = players[name]
    fun getPlayerSession(player: ServerPlayerEntity): ServerPlayerSession = players[player.name.string]!!

    fun handleConnected(player: ServerPlayerEntity) {
        players.put(player.name.string, ServerPlayerSession(player, null)
            .also { it.onConnect() })
    }

    fun handleDisconnected(player: ServerPlayerEntity) {
        players.get(player.name.string)!!.onDisconnect()
        players.remove(player.name.string)
    }

    fun getDefaultSpeed(gameMode: GameMode): Int {
        return defaultSpeeds[gameMode]!!
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        PlayerManagerCommand().register(dispatcher)
        LoginCommand().register(dispatcher)
        NicknameCommand().register(dispatcher)
        AccountSyncCommand().register(dispatcher)
    }

    fun getPlayerLookingAt(player: ServerPlayerEntity): ServerPlayerEntity? {
        val world = player.world
        val lookDirection = player.rotationVector // Предполагается, что это направление взгляда
        val start = player.eyePos // Начальная точка луча (глаза игрока)
        val end = start.add(lookDirection.multiply(5.0)) // Конечная точка луча (5 блоков)

        val entities = world.server!!.playerManager.playerList.getPlayersInRadius(player, 5.0)

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