package org.qbrp.core.game.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
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
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.annotations.SubCommand
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.player.registration.LoginCommand
import org.qbrp.core.game.player.registration.RegistrationCommand
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.ConfigUpdateCallback
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.secrets.Databases

@Command("playermanager")
object PlayerManager: ServerModCommand {
    private lateinit var defaultSpeeds: Map<GameMode, Int>

    val players: MutableMap<String, ServerPlayerSession> = mutableMapOf()
    val playersList
        get() = players.values.toList()
    val databaseService = DatabaseService(Databases.MAIN, "players").also { it.connect() }

    init {
        ConfigUpdateCallback.EVENT.register {
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
        players.put(player.name.string, ServerPlayerSession(player, getDefaultSpeed(player.interactionManager.gameMode).toInt())
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
        dispatcher.register(
            CommandBuilder()
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
        RegistrationCommand().register(dispatcher)
        LoginCommand().register(dispatcher)
        NicknameCommand().register(dispatcher)
    }

    fun getPlayerLookingAt(player: ServerPlayerEntity): Entity? {
        val world = player?.world
        val lookDirection = getLookDirection(player!!) // Получаем направление взгляда
        val raycastResult = world?.raycast(
            RaycastContext(
                player.eyePos,
                player.eyePos.add(lookDirection.multiply(5.0)), // Дистанция взгляда
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
            )
        )

        if (raycastResult?.type == HitResult.Type.ENTITY) {
            val entityHitResult = raycastResult as EntityHitResult
            return entityHitResult.entity
        }

        return null
    }


    fun getLookDirection(player: PlayerEntity): Vec3d {
        val rotation = player.rotationVector
        return Vec3d(rotation.x, rotation.y, rotation.z)
    }

    @SubCommand
    class Edit(@Arg val playerName: String) {

        @SubCommand
        class Speed {

            @SubCommand
            class Set(@Arg(sub = true) val playerName: String, @Arg var speed: Int): CallbackCommand() {
                @Execute(operatorLevel = 4)
                fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
                    getPlayerSession(playerName)?.setSpeed(speed)
                }
            }

            @SubCommand
            class Reset(@Arg(sub = true) val playerName: String,): CallbackCommand() {
                @Execute(operatorLevel = 4)
                fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
                    getPlayerSession(playerName)?.resetSpeed()
                }
            }

        }

    }
}