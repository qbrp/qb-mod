package org.qbrp.engine.music.plasmo.model.audio

import klite.Server
import org.qbrp.engine.music.plasmo.controller.view.View
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import org.qbrp.engine.music.plasmo.playback.player.PlayerState
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.audio.source.ServerBroadcastSource
import su.plo.voice.api.server.player.VoiceServerPlayer
import su.plo.voice.lavaplayer.libs.com.fasterxml.jackson.annotation.JsonIgnore
import java.util.concurrent.CopyOnWriteArrayList

abstract class Playable(voiceServer: PlasmoVoiceServer) {
    abstract var name: String
    abstract var selector: Selector
    abstract var priority: Priority

    var isPlaying: Boolean = true
    var isManuallyDisabled: Boolean = false // Флаг для ручного выключения
    var isAutoDisabled: Boolean = false

    private val sourceLine = voiceServer.sourceLineManager
        .getLineByName("music")
        .orElseThrow { IllegalStateException("Proximity source line not found") }
    private var broadcastSource = sourceLine.createBroadcastSource(true).apply { players = emptyList() }

    private val subscribedPlayers = CopyOnWriteArrayList<VoiceServerPlayer>()
    lateinit var queue: Queue

    fun checkAutoDisable() {
        if (subscribedPlayers.isEmpty() && !isAutoDisabled) {
            isAutoDisabled = true
            disable()
        }
    }

    fun disable() {
        subscribedPlayers.clear()
        broadcastSource.players = emptyList()
        queue.clearRadio()
        logger.log("Плейлист $name выключен")
    }

    fun enable() {
        broadcastSource.players = subscribedPlayers
        queue.play()
        logger.log("Плейлист $name включен")
    }

    fun initQueue(queue: Queue) {
        this.queue = queue.apply { source = broadcastSource }
    }

    fun subscribe(player: PlayerState) {
        if (!isManuallyDisabled) {
            if (isAutoDisabled) enable(); isAutoDisabled = false
            subscribedPlayers.add(player.voicePlayer)
        }
        logger.log("${player.player.name.string} подписан на $name")
    }

    fun unsubscribe(player: PlayerState) {
        subscribedPlayers.remove(player.voicePlayer)
        checkAutoDisable()
    }

    abstract fun getView(): View

    fun toDTO(): PlayableDTO {
        return PlayableDTO(
            name = this.name,
            selector = this.selector,
            priority = this.priority,
            queue = this.queue,
            isManuallyDisabled = this.isManuallyDisabled,
        )
    }

    companion object {
        private val logger = Loggers.get("musicManager", "playback")
    }
}
