package org.qbrp.engine.music

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.ServerCore
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.music.plasmo.*
import org.qbrp.engine.music.plasmo.controller.PlaylistCommand
import org.qbrp.engine.music.plasmo.controller.TrackCommand
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.Playlist
import org.qbrp.engine.music.plasmo.model.audio.Queue
import org.qbrp.engine.music.plasmo.model.audio.Track
import org.qbrp.engine.music.plasmo.model.audio.playback.PlaybackSessionManager
import org.qbrp.engine.music.plasmo.model.audio.playback.PlaybackSessionManagerImpl
import org.qbrp.engine.music.plasmo.model.audio.playback.Radio
import org.qbrp.engine.music.plasmo.model.audio.shadow.ShadowQueue
import org.qbrp.engine.music.plasmo.model.priority.Priorities
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import org.qbrp.engine.music.plasmo.playback.PlayerSelectionHandler
import org.qbrp.engine.music.plasmo.playback.player.MusicPlayerManager
import org.qbrp.engine.music.plasmo.playback.player.PlayerState
import org.qbrp.engine.music.plasmo.view.MusicViewCommand
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.audio.source.ServerDirectSource

@Autoload
class MusicManagerModule : QbModule("music"), KoinComponent {
    private val logger = Loggers.get("musicManager")

    override fun getName() = "musicManager"

    override fun load() {
        get<MusicDatabaseService>().db.connect()
        get<MusicStorage>().apply {
            loadFromDatabase()
        }
        get<PlayerSelectionHandler>().startHandling()
        get<MusicAddonLoader>()
        CommandsRepository.add(get<List<ServerModCommand>>())
    }

    override fun getAPI(): ModuleAPI? = null

    fun getVoiceServer(): PlasmoVoiceServer = get<MusicAddonLoader>().voiceServer

    override fun getKoinModule() = module {
        single { ServerCore.server } // Регистрируем MinecraftServer
        single { ServerResources.getRootBranch().config.music }
        single { ServerResources.getRootBranch().config.databases }
        single {
            MusicDatabaseService(
                db = DatabaseService(get<ServerConfigData.Databases>().nodeUri, "music")
            )
        }
        single { MusicPlayerManager(get()) }
        single { MusicManagerEvents(get()) }
        single {
            MusicAddonLoader(
                moduleName = "musicManager",
                server = get<MinecraftServer>(),
                events = get<MusicManagerEvents>()
            ).also { PlasmoVoiceServer.getAddonsLoader().load(it); }
        }
        single { get<MusicAddonLoader>().voiceServer }
        single {
            Priorities().apply { fromStrings(get<ServerConfigData.Music>().priorities) }
        }
        single { MusicStorage(get(), get(), get()) }
        single { PlayerSelectionHandler(get(), get(), get()) }
        single {
            listOf(
                PlaylistCommand(get(), get()),
                TrackCommand(get()),
                MusicViewCommand(get())
            )
        }
        factory { (source: ServerDirectSource, track: Track, onFinishedEvent: () -> Unit) ->
            Radio(get(), source, track, onFinishedEvent)
        }
        factory { (currentTrackIndex: Int, repeats: Int, tracks: MutableList<String>) ->
            Queue(currentTrackIndex, repeats, tracks)
        }
        factory { (player: ServerPlayerEntity) -> PlayerState(get(), player) }
        factory { (originalName: String) -> ShadowQueue(originalName) }
        factory { (playable: Playable) -> PlaybackSessionManagerImpl(playable, get()) }

        factory { (name: String, selector: Selector, priority: Priority, queue: Queue) ->
            Playlist(name, selector, priority, get(), get()).apply { loadQueue(queue) }
        }

        single { MusicManagerModule() }
    }
}