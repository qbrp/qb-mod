package org.qbrp.main.core.mc.player

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.exception.PlayerNotFoundException
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode.ADVENTURE
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode.CREATIVE
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode.SPECTATOR
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode.SURVIVAL
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.PlayerModeration
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.core.mc.server.command.RawMinecraftCommand
import org.qbrp.main.core.mc.server.command.ServerCommandExecutor
import org.qbrp.main.core.mc.text.MinecraftText
import org.qbrp.main.core.mc.text.map
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.networking.DisconnectReason
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

fun PlayerReference.disconnect(reason: DisconnectReason, connectionService: PlayerModeration) {
    connectionService.disconnect(uuid, reason)
}

context(ctx: MinecraftContext)
fun PlayerReference.disconnect(reason: DisconnectReason) {
    ctx.serverThread.execute { disconnect(reason, ctx.playerModeration) }
}

context(ctx: MinecraftContext)
fun MinecraftPlayerId.getEntity(): ServerPlayerEntity {
    return getEntity(ctx.playerManager)
}

fun MinecraftPlayerId.getEntity(minecraftPlayerManager: MinecraftPlayerManager): ServerPlayerEntity {
    return minecraftPlayerManager.getById(this) ?: throw PlayerNotFoundException(this)
}

fun PlayerReference.getEntity(minecraftPlayerManager: MinecraftPlayerManager): ServerPlayerEntity {
    return uuid.getEntity(minecraftPlayerManager)
}

context(ctx: MinecraftContext)
fun PlayerReference.getEntity(): ServerPlayerEntity {
    return uuid.getEntity(ctx.playerManager)
}

fun PlayerReference.exists(playerManager: MinecraftPlayerManager): Boolean {
    return playerManager.getById(uuid) != null
}

fun PlayerReference.executeCommand(command: RawMinecraftCommand, commandExecutor: ServerCommandExecutor) {
    return commandExecutor.executeAsPlayer(uuid, command)
}

context(ctx: MinecraftContext)
fun PlayerReference.executeCommand(command: RawMinecraftCommand) {
    ctx.serverThread.execute { executeCommand(command, ctx.commandExecutor) }
}

context(ctx: MinecraftContext)
val PlayerReference.isOp
    get() = ctx.permissionService.isOp(this.uuid)

fun PlayerReference.sendMessage(text: MinecraftText, playerManager: MinecraftPlayerManager) {
    uuid.getEntity(playerManager).sendMessage(text.map())
}

context(ctx: MinecraftContext)
fun PlayerReference.sendMessage(text: MinecraftText) {
    ctx.serverThread.execute { sendMessage(text, ctx.playerManager) }
}

fun PlayerReference.setSpeedModifier(value: Double, service: MinecraftPlayerAttributeService, playerManager: MinecraftPlayerManager) {
    service.updateSpeedModifier(uuid.getEntity(playerManager), value)
}

context(ctx: MinecraftContext)
fun PlayerReference.setSpeedModifier(value: Double) {
    ctx.serverThread.execute { setSpeedModifier(value, ctx.attributeService, ctx.playerManager) }
}

fun PlayerReference.raycastBoundingBox(min: Position, max: Position, playerManager: MinecraftPlayerManager): Vec3Adapter? {
    val entity = uuid.getEntity(playerManager)
    return entity.boundingBox.raycast(min.minecraft(), max.minecraft()).getOrNull()?.let { Vec3Adapter(it) }
}

context(ctx: MinecraftContext)
fun PlayerReference.raycastBoundingBox(min: Position, max: Position): Vec3Adapter? {
    return raycastBoundingBox(min, max, ctx.playerManager)
}

internal fun PlayerReference.setGameMode(gameMode: GameMode, playerManager: MinecraftPlayerManager) {
    uuid.getEntity(playerManager).changeGameMode(gameMode)
}

context(ctx: MinecraftContext)
fun PlayerReference.setGameMode(gameMode: MinecraftGameMode) {
    ctx.serverThread.execute { setGameMode(gameMode.toMinecraft(), ctx.playerManager) }
}

internal fun MinecraftGameMode.toMinecraft(): GameMode {
    return when(this) {
        CREATIVE -> GameMode.CREATIVE
        SURVIVAL -> GameMode.SURVIVAL
        SPECTATOR -> GameMode.SPECTATOR
        ADVENTURE -> GameMode.ADVENTURE
    }
}

@Deprecated("")
fun ServerPlayerEntity.hasPermission(permission: String, service: PermissionService = GlobalContext.get().get()): Boolean {
    return service.hasPermission(MinecraftPlayerId(uuid), Permission(permission))
}

fun ServerPlayerObject.hasPermission(permission: Permission, service: PermissionService = GlobalContext.get().get()): Boolean {
    return service.hasPermission(uuid, permission)
}

context(minecraft: MinecraftContext)
fun ServerPlayerObject.getPermissions() = minecraft.permissionService.permissionsOf(uuid)

fun PlayerReference.hasPermission(permission: Permission, service: PermissionService = GlobalContext.get().get()): Boolean {
    return service.hasPermission(uuid, permission)
}

fun UUID.toPlayerMc() = MinecraftPlayerId(this)

fun ServerPlayerEntity.asReference() = ServerPlayerEntityReference(MinecraftPlayerId(uuid))

data class ServerPlayerEntityReference(override val uuid: MinecraftPlayerId): PlayerReference