package org.qbrp.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonManagedReference
import net.minecraft.server.network.ServerPlayerEntity
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

abstract class Playable(
    @JsonIgnore val voiceServer: PlasmoVoiceServer
) {
    abstract var name: String
    abstract var selector: Selector
    abstract var priority: Priority

    var isManuallyDisabled: Boolean = false // Флаг для ручного выключения

    @JsonIgnore
    private val sourceLine = voiceServer.sourceLineManager
        .getLineByName("music")
        .orElseThrow { IllegalStateException("Music source line not found") }

    @JsonIgnore
    private val playerSessions = ConcurrentHashMap<VoiceServerPlayer, PlayerSession>()
    @JsonManagedReference open lateinit var queue: Queue

    open fun loadQueue(queue: Queue) { this.queue = queue }

    abstract fun getView(): View

    fun disable() {
        isManuallyDisabled = true
        logger.log("Плейлист $name выключен")
    }

    fun enable() {
        isManuallyDisabled = false
        logger.log("Плейлист $name включен")
    }

    @JsonIgnore
    private val playerSessionsLock = Any()
    fun doForSessions(block: (VoiceServerPlayer, PlayerSession) -> Unit) {
        synchronized(playerSessionsLock) {
            playerSessions.forEach(block)
        }
    }


    fun getSession(player: VoiceServerPlayer): PlayerSession? = playerSessions[player]
    fun getSession(player: ServerPlayerEntity): PlayerSession? = playerSessions.values.find { it.source.player.instance.gameProfile.name == player.name.string }
    fun validateStaticState() {
        doForSessions { player, session ->
            val cachedTrackIndex = session.queue.currentTrackIndex
            session.queue = queue
            session.queue.currentTrackIndex = cachedTrackIndex
            session.playable = this
        }
    }

    fun calculateSyncedQueue(): Queue {
        if (playerSessions.isEmpty()) return queue
        val playingTracks: MutableMap<Queue, Track> = mutableMapOf()
        doForSessions { _, session -> playingTracks[session.queue] = session.queue.getCurrentTrack() ?: return@doForSessions }
        val trackFrequency = playingTracks.values.groupingBy { it }.eachCount()
        val mostFrequentTrack = trackFrequency.maxByOrNull { it.value }?.key ?: return queue
        val lastMatchingQueue = playingTracks.entries.lastOrNull { it.value == mostFrequentTrack }?.key
        return lastMatchingQueue ?: queue
    }

    fun subscribe(playerState: PlayerState): Boolean {
        val player = playerState.voicePlayer
        if (isManuallyDisabled) return false
        playerSessions[player]?.let { it.cancelUnsubscribe(); return true }

        val source = sourceLine.createDirectSource(player, true)
        val session = PlayerSession(this, source, calculateSyncedQueue())
        playerSessions[player] = session.apply {
            createRadio()
        }

        logger.log("${player.instance.name} подписан на $name")
        return true
    }

    fun unsubscribe(player: PlayerState): Boolean {
        val session = getSession(player.voicePlayer) ?: return true
        val isReadyToUnsubscribe = session.handleUnsubscribe()
        if (isReadyToUnsubscribe) {
            killSession(player.voicePlayer)
            return true
        }
        return false
    }

    fun killSession(player: VoicePlayer) {
        playerSessions[player]?.destroy()
        playerSessions.remove(player)
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
