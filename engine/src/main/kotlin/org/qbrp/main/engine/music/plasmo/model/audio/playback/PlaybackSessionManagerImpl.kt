package org.qbrp.main.engine.music.plasmo.model.audio.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.engine.music.plasmo.model.audio.Playable
import org.qbrp.main.engine.music.plasmo.model.audio.Queue
import org.qbrp.main.engine.music.plasmo.model.audio.Track
import org.qbrp.main.engine.music.plasmo.playback.player.PlayerState
import org.qbrp.main.core.utils.log.LoggerUtil
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.player.VoicePlayer
import su.plo.voice.api.server.player.VoiceServerPlayer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.get
import kotlin.collections.remove

class PlaybackSessionManagerImpl(val playable: Playable, voiceServer: PlasmoVoiceServer): PlaybackSubscribe, PlaybackSessionManager {
    private val sourceLine = voiceServer.sourceLineManager
        .getLineByName("music")
        .orElseThrow { IllegalStateException("Music source line not found") }

    private val playerSessions = ConcurrentHashMap<VoiceServerPlayer, PlayerSession>()

    private val playerSessionInitializations = CopyOnWriteArrayList<PlayerState>()

    fun getSession(player: VoiceServerPlayer): PlayerSession? = playerSessions[player]
    override fun getSession(player: ServerPlayerEntity): PlayerSession? = playerSessions.values.find { it.source.player.instance.gameProfile.name == player.name.string }

    override fun subscribe(playerState: PlayerState): Boolean {
        val player = playerState.voicePlayer
        if (playable.isManuallyDisabled) return false
        if (playerSessionInitializations.find { playerState == it } != null ) { return true }
        playerSessions[player]?.let { it.cancelUnsubscribe(); return true }

        CoroutineScope(Dispatchers.IO).launch {
            playerSessionInitializations.add(playerState)
            val source = sourceLine.createDirectSource(player, true)
            val session = PlayerSession(playable, source, calculateQueueForSync(), playerState)

            playerSessions[player] = session.apply {
                createRadio()
            }
            logger.log("${player.instance.name} подписан на ${playable.name}")
            playerSessionInitializations.removeIf { it == playerState }
        }
        return true
    }

    override fun unsubscribe(player: PlayerState): Boolean {
        val session = getSession(player.voicePlayer) ?: return true
        val isReadyToUnsubscribe = session.handleUnsubscribe()
        if (isReadyToUnsubscribe) {
            destroySession(player.voicePlayer)
            return true
        }
        return false
    }

    override fun destroySession(player: VoicePlayer) {
        playerSessions[player]?.destroy()
        playerSessions.remove(player)
    }

    override fun doForSessions(block: (VoiceServerPlayer, PlayerSession) -> Unit) {
        playerSessions.forEach(block)
    }

    override suspend fun doForSessionsAsync(block: suspend (VoiceServerPlayer, PlayerSession) -> Unit) {
        coroutineScope {
            playerSessions.forEach { (key, value) ->
                launch { block(key, value) } // Запускаем в корутине
            }
        }
    }

    private fun calculateQueueForSync(): Queue {
        if (playerSessions.isEmpty()) return playable.queue.copy()
        val playingTracks: MutableMap<Queue, Track> = mutableMapOf()
        doForSessions { _, session -> playingTracks[session.queue] = session.queue.getCurrentTrack() ?: return@doForSessions }
        val trackFrequency = playingTracks.values.groupingBy { it }.eachCount()
        val mostFrequentTrack = trackFrequency.maxByOrNull { it.value }?.key ?: return playable.queue.copy()
        val lastMatchingQueue = playingTracks.entries.lastOrNull { it.value == mostFrequentTrack }?.key
        return lastMatchingQueue?.copy() ?: playable.queue.copy()
    }

    companion object {
        private val logger = LoggerUtil.get("musicManager", "playback")
    }
}