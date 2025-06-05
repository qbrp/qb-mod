package org.qbrp.main.core.mc.player

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.qbrp.main.core.database.Databases
import org.qbrp.main.core.game.prefabs.PrefabField
import org.qbrp.main.core.game.prefabs.RuntimePrefab
import org.qbrp.main.core.game.prefabs.RuntimePrefabStorage
import org.qbrp.main.core.mc.player.service.AccountDatabaseService
import org.qbrp.main.core.mc.player.service.PlayerLifecycleManager
import org.qbrp.main.core.mc.player.service.PlayerSerializer
import org.qbrp.main.core.mc.player.service.PlayerStorage
import org.qbrp.main.core.mc.player.registration.AccountSyncCommand
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.storage.StorageAPI
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.core.utils.networking.messages.Messages.AUTH
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.engine.ModInitializedEvent
import org.qbrp.main.core.game.GameAPI
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.engine.players.characters.appearance.Appearance
import org.qbrp.main.engine.players.nicknames.NicknameCommand
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.game.saving.ServerStopSaver
import org.qbrp.main.core.game.saving.TimerSaver
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.mc.player.service.PlayerDisconnectEvent

@Autoload(LoadPriority.LOWEST, EnvType.SERVER)
class PlayersModule: GameModule("players"), CommandRegistryEntry, KoinComponent, PlayersAPI {
    override val storage: PlayerStorage get() = getLocal()
    companion object {
        val PLAYER_PREFAB = RuntimePrefab("player", "load")
    }

    override fun getKoinModule(): Module = innerWithApi({
        single<PlayersAPI> { this@PlayersModule }
        factory(named("player-tag")) { PLAYER_PREFAB.getDefaultTag() }
    },
        {
        scoped { AccountDatabaseService(
            "data",
            "players",
            Databases.MAIN_ASYNC
        ) }
        scoped { PlayerStorage() }
        scoped<TableAccess> { get<StorageAPI>().getTable("players") }
        scoped { PlayerSerializer(get(), get(named("player-tag")))}
        scoped<Saver<PlayerObject>> { Saver<PlayerObject> { getLocal<TableAccess>().saveObject(it.id, getLocal<PlayerSerializer>().toJson(it)) } }
        scoped { PlayerLifecycleManager(get(), get(), get(), get(), get()) }
    })

    override fun onEnable() {
        get<CommandsAPI>().add(this)
        val saver = getLocal<Saver<PlayerObject>>()
        val storage = getLocal<PlayerStorage>()
        TimerSaver<PlayerObject>("Players", 4000L * 60L)
            .run(storage, saver)
        ServerStopSaver<PlayerObject>()
            .run(storage, saver)

        val lifecycleManager = getLocal<PlayerLifecycleManager>()
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            PlayerDisconnectEvent.EVENT.invoker().onDisconnect(getPlayerSession(handler.player), this)
            lifecycleManager.handleDisconnected(handler.player)
        }
        ServerReceiver<ServerReceiverContext>(AUTH, StringContent::class, { message, context, receiver ->
            lifecycleManager.handleAuth(context.player, message.getContent())
            true
        }).register()

        val tickTask = object : Tick<ServerWorld> {
            override fun tick(context: ServerWorld) {
                storage.getAll().forEach { it.tick(it.entity.world as ServerWorld) }
            }
        }
        get<GameAPI>().addWorldTickTask(tickTask)

        ModInitializedEvent.EVENT.register {
            PLAYER_PREFAB.components += PrefabField { Appearance() }
            get<RuntimePrefabStorage>().addPrefab(PLAYER_PREFAB)
        }
    }

    override fun getPlayerSession(name: String): PlayerObject? = storage.getByPlayerName(name)
    override fun getPlayerSession(player: ServerPlayerEntity): PlayerObject = storage.getByPlayer(player)
    override fun getPlayerSessionOrNull(player: ServerPlayerEntity): PlayerObject? = storage.getByPlayerOrNull(player)
    override fun getPlayers(): Collection<PlayerObject> = storage.getAll()

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        NicknameCommand().register(dispatcher)
        AccountSyncCommand(getLocal()).register(dispatcher)
    }
}