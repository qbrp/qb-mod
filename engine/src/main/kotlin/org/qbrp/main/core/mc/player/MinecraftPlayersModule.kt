package org.qbrp.main.core.mc.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.game.GameTicker
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.context.withMinecraftContext
import org.qbrp.main.core.mc.player.events.AsyncMinecraftPlayerJoinEvent
import org.qbrp.main.core.mc.player.events.MinecraftPlayerLeaveEvent
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.mc.player.permission.PermissionServiceImpl
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.MinecraftServerPlayerManager
import org.qbrp.main.core.mc.player.service.PlayerConnectionService
import org.qbrp.main.core.mc.player.service.PlayerModeration
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeServiceImpl
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.core.mc.player.service.snapshot.ServerPlayerSnapshotService
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.model.networking.DisconnectReason
import java.util.List

@Autoload
class MinecraftPlayersModule: EngineModule("minecraft-players") {
    override fun getKoinModule(): Module = innerWithApi({
        single<PlayerModeration> { PlayerConnectionService(get(), get()) }
        single<PermissionService> { PermissionServiceImpl(get(), get()) }
        single<MinecraftPlayerAttributeService> { getLocal<MinecraftPlayerAttributeServiceImpl>() }
        single<MinecraftPlayerSnapshotService> { getLocal<ServerPlayerSnapshotService>() }
        single { MinecraftServerPlayerManager(get()) }
        single<MinecraftPlayerManager> { get<MinecraftServerPlayerManager>() }
    }, {
        scoped { MinecraftPlayerAttributeServiceImpl(get()) }
        scoped { ServerPlayerSnapshotService(get(), get(), get()) }
    })

    val joinCoroutineScope = CoroutineScope(Dispatchers.Default)
    val playerManager: MinecraftServerPlayerManager by inject()
    val playerFactory: ServerPlayerSnapshotService by lazy { getLocal() }
    val attributeService: MinecraftPlayerAttributeServiceImpl by lazy { getLocal() }
    val permissionService: PermissionService by inject()
    val thread by inject<ServerThread>()

    override fun onModLoaded() = withMinecraftContext {
        var counter = 0
        get<GameTicker>().addWorldTickTask {
            if (counter++ > 20) {
                playerManager.players.forEach {
                    permissionService.sendPermissionsPacket(it)
                }
                counter = 0
            }
        }
        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            emitJoin(handler.player)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            emitLeave(handler.player)
        }
    }

    context(ctx: MinecraftContext)
    private fun emitJoin(entity: ServerPlayerEntity) = withEventContext {
        val snapshot = playerFactory.lazySnapshotPlayer(entity)
        attributeService.instantiateDefaultAttributeStates(entity, 0.0)
        joinCoroutineScope.launch {
            try {
                val server = entity.server
                val mcPlayerManager = server.playerManager
                playerManager.onEmit(snapshot, entity)
                AsyncMinecraftPlayerJoinEvent(
                    snapshot,
                    thread,
                    playerManager
                ).emit {
                    if (cancelled && mcPlayerManager.getPlayer(entity.uuid) != null) {
                        entity.asReference().disconnect(DisconnectReason("Свяжитесь с администратором."))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                entity.asReference().disconnect(DisconnectReason("Возникла ошибка при входе на сервер: ${e.message}.\nСвяжитесь с администратором."))
            }
        }
    }

    context(ctx: MinecraftContext)
    private fun emitLeave(entity: ServerPlayerEntity) = withEventContext {
        MinecraftPlayerLeaveEvent(
            playerFactory.lazySnapshotPlayer(entity),
            thread
        ).emit()
    }
}