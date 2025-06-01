package org.qbrp.main.engine.music

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.engine.music.plasmo.*
import org.qbrp.main.engine.music.plasmo.controller.PlaylistCommand
import org.qbrp.main.engine.music.plasmo.controller.TrackCommand
import org.qbrp.main.engine.music.plasmo.model.audio.Playable
import org.qbrp.main.engine.music.plasmo.model.audio.Playlist
import org.qbrp.main.engine.music.plasmo.model.audio.Queue
import org.qbrp.main.engine.music.plasmo.model.audio.Track
import org.qbrp.main.engine.music.plasmo.model.audio.playback.PlaybackSessionManagerImpl
import org.qbrp.main.engine.music.plasmo.model.audio.playback.Radio
import org.qbrp.main.engine.music.plasmo.model.audio.shadow.ShadowQueue
import org.qbrp.main.engine.music.plasmo.model.priority.Priorities
import org.qbrp.main.engine.music.plasmo.model.priority.Priority
import org.qbrp.main.engine.music.plasmo.model.selectors.Selector
import org.qbrp.main.engine.music.plasmo.playback.PlayerSelectionHandler
import org.qbrp.main.engine.music.plasmo.playback.lavaplayer.AudioManager
import org.qbrp.main.engine.music.plasmo.playback.player.MusicPlayerManager
import org.qbrp.main.engine.music.plasmo.playback.player.PlayerState
import org.qbrp.main.engine.music.plasmo.view.MusicViewCommand
import org.qbrp.main.core.database.DatabaseService
import org.qbrp.main.core.database.SynchronousDatabaseClient
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.log.LoggerUtil
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.audio.source.ServerDirectSource
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload
class MusicManagerModule : QbModule("music"), KoinComponent, MusicManagerAPI {
    private val logger = LoggerUtil.get("musicManager")

    override fun getTracks(): List<Track> = getLocal<MusicStorage>().getAllTracks()

    override fun onLoad() {
        AudioManager.registerSources()
        getLocal<MusicStorage>().apply {
            loadFromDatabase()
        }
        getLocal<PlayerSelectionHandler>().startHandling()
        getLocal<MusicAddonLoader>()
        get<CommandsAPI>().add(getLocal<List<CommandRegistryEntry>>())
    }

    fun getVoiceServer(): PlasmoVoiceServer = getLocal<MusicAddonLoader>().voiceServer

    override fun getKoinModule() = module {
        single { Core.server } // Регистрируем MinecraftServer
        single { get<ServerConfigData>().music }
        single { get<ServerConfigData>().databases }
        single {
            MusicDatabaseService(
                db = DatabaseService(SynchronousDatabaseClient(get<ServerConfigData.Databases>().nodeUri), "music")
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
            Playlist(name, selector, priority, get()).apply { loadQueue(queue) }
        }

        single { MusicManagerModule() }
    }
}