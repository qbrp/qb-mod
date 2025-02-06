package org.qbrp.engine.music

import net.minecraft.server.MinecraftServer
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.music.plasmo.MusicDatabaseService
import org.qbrp.engine.music.plasmo.MusicStorage
import org.qbrp.engine.music.plasmo.MusicAddonLoader
import org.qbrp.core.plasmo.PlasmoEventListener
import org.qbrp.engine.music.plasmo.controller.PlaylistCommand
import org.qbrp.engine.music.plasmo.controller.TrackCommand
import org.qbrp.engine.music.plasmo.controller.view.MusicViewCommand
import org.qbrp.engine.music.plasmo.model.priority.Priorities
import org.qbrp.engine.music.plasmo.model.selectors.SelectorBuilder
import org.qbrp.engine.music.plasmo.playback.PlayerSelectionHandler
import org.qbrp.engine.music.plasmo.playback.player.MusicPlayerManager
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.PlasmoVoiceServer

class MusicManagerModule(
    server: MinecraftServer,
    val musicConfig: ServerConfigData.Music,
    val databaseConfig: ServerConfigData.Databases
) {
    private val logger = Loggers.get("musicManager")
    init { logger.log("Загрузка <<MusicManagerModule>>") }
    private var selectorBuilder: SelectorBuilder = SelectorBuilder()
    private var priorities: Priorities = Priorities().apply {
        fromStrings(musicConfig.priorities)
    }
    private var database: MusicDatabaseService = MusicDatabaseService(
        DatabaseService(databaseConfig.nodeUri, databaseConfig.music).apply { connect() })
    private val musicPlayerManager: MusicPlayerManager = MusicPlayerManager(server.playerManager)
    private val eventListener: PlasmoEventListener = MusicManagerEvents(musicPlayerManager)
    private val loader: MusicAddonLoader = MusicAddonLoader("musicManager", server, eventListener).also {
        PlasmoVoiceServer.getAddonsLoader().load(it)
    }
    var storage: MusicStorage = MusicStorage(database, priorities, loader.voiceServer)
    private var selectionHandler: PlayerSelectionHandler = PlayerSelectionHandler(musicPlayerManager, storage, priorities).apply {
        startHandling()
    }
    private var commands = listOf(
        PlaylistCommand(priorities, storage),
        TrackCommand(storage),
        MusicViewCommand(selectionHandler)
        ).also { CommandsRepository.add(it) }

    fun getVoiceServer(): PlasmoVoiceServer = loader.voiceServer

    fun load() {
        storage.loadFromDatabase()
        //storage.addDefaultPlaylist()
        storage.startSaveLifecycle()
        logger.success("MusicManagerModule загружен")
    }
}
