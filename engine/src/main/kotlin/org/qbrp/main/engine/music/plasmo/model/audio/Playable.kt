package org.qbrp.main.engine.music.plasmo.model.audio

import org.koin.core.context.GlobalContext
import org.qbrp.main.engine.music.plasmo.MusicStorage
import org.qbrp.main.engine.music.plasmo.model.audio.playback.PlaybackSessionManager
import org.qbrp.main.engine.music.plasmo.model.audio.playback.PlaybackSessionManagerImpl
import org.qbrp.main.engine.music.plasmo.model.audio.playback.PlaybackSubscribe
import org.qbrp.main.engine.music.plasmo.view.View
import org.qbrp.main.engine.music.plasmo.model.priority.Priority
import org.qbrp.main.engine.music.plasmo.model.selectors.Selector
import org.qbrp.main.engine.music.plasmo.playback.player.PlayerState
import org.qbrp.main.core.utils.log.LoggerUtil
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.lavaplayer.libs.com.fasterxml.jackson.annotation.JsonIgnore

abstract class Playable(
    @JsonIgnore val voiceServer: PlasmoVoiceServer
): PlaybackSubscribe {
    abstract var name: String
    abstract var selector: Selector
    abstract var priority: Priority
    open val sessionManager: PlaybackSessionManager = PlaybackSessionManagerImpl(this, voiceServer)
    open lateinit var queue: Queue

    var isManuallyDisabled: Boolean = false // Флаг для ручного выключения

    open fun loadQueue(queue: Queue) { this.queue = queue }

    abstract fun getView(): View

    abstract fun onUpdate()

    override fun subscribe(playerState: PlayerState): Boolean = sessionManager.subscribe(playerState)
    override fun unsubscribe(playerState: PlayerState): Boolean = sessionManager.unsubscribe(playerState)

    open fun save() {
        GlobalContext.get().get<MusicStorage>().save(this)
    }

    fun disable() {
        isManuallyDisabled = true
        logger.log("Плейлист $name выключен")
    }

    fun enable() {
        isManuallyDisabled = false
        logger.log("Плейлист $name включен")
    }

    open fun toDTO(): PlayableDTO {
        return PlayableDTO(
            type = "playlist",
            name = this.name,
            selector = this.selector,
            priority = this.priority,
            queue = this.queue,
            isManuallyDisabled = this.isManuallyDisabled,
        )
    }

    companion object {
        private val logger = LoggerUtil.get("musicManager", "playback")
    }
}
