package org.qbrp.engine.music.plasmo.model.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.music.plasmo.model.audio.playback.PlaybackSessionManager
import org.qbrp.engine.music.plasmo.model.audio.playback.PlaybackSessionManagerImpl
import org.qbrp.engine.music.plasmo.model.audio.playback.PlaybackSubscribe
import org.qbrp.engine.music.plasmo.view.View
import org.qbrp.engine.music.plasmo.model.audio.playback.PlayerSession
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import org.qbrp.engine.music.plasmo.playback.player.PlayerState
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.player.VoicePlayer
import su.plo.voice.api.server.player.VoiceServerPlayer
import su.plo.voice.lavaplayer.libs.com.fasterxml.jackson.annotation.JsonIgnore
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

abstract class Playable(
    @JsonIgnore val voiceServer: PlasmoVoiceServer
): PlaybackSubscribe {
    abstract var name: String
    abstract var selector: Selector
    abstract var priority: Priority
    abstract var sessionManager: PlaybackSessionManager

    var isManuallyDisabled: Boolean = false // Флаг для ручного выключения

    open lateinit var queue: Queue
    open fun loadQueue(queue: Queue) { this.queue = queue }

    abstract fun getView(): View

    abstract fun onUpdate()

    override fun subscribe(playerState: PlayerState): Boolean = sessionManager.subscribe(playerState)
    override fun unsubscribe(playerState: PlayerState): Boolean = sessionManager.unsubscribe(playerState)

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
        private val logger = Loggers.get("musicManager", "playback")
    }
}
