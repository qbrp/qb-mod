package org.qbrp.plasmo.contoller.player

import org.qbrp.plasmo.contoller.MusicFrameProvider
import org.qbrp.plasmo.contoller.lavaplayer.AudioManager
import org.qbrp.plasmo.model.audio.Track
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.audio.source.AudioSender
import su.plo.voice.api.server.player.VoicePlayer
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlin.math.abs

class PlayerController(nickname: String) {
    companion object { val logger = Loggers.get("plasmo", "controller")}

    private val sourceLine = SourceManager.voiceServer.sourceLineManager
        .getLineByName("music")
        .orElseThrow { IllegalStateException("Proximity source line not found") }
    private val voicePlayer: VoicePlayer = SourceManager.voiceServer.playerManager
        .getPlayerByName(nickname)
        .orElseThrow { IllegalStateException("Player not found") }
    private val source = sourceLine.createDirectSource(voicePlayer, true)

    fun remove() { source.remove(); stopTrack() }

    private var currentSessionUUID: String = ""
    private var currentTrack: AudioTrack? = null
    private var currentPlayer: AudioPlayer? = null
    private lateinit var sender: AudioSender

    fun sync(track: Track, position: Int, session: String) {
        val isTrackChanged = session != currentSessionUUID
        if (isTrackChanged) {
            logger.log("Рассинхронизирован ${track.name}: UUID $currentSessionUUID слушателя не совпадает с $session")
            playTrack(track, position, session)
        }
        val currentPosition = currentPlayer?.playingTrack?.position ?: 0
        val isPositionChanged = abs(position - (currentPosition / 1000)) > 5
        if (isPositionChanged) {
            logger.log("Рассинхронизирован ${track.name}: текущая позиция ${(currentPosition / 1000)}, настоящая $position")
            currentPlayer?.playingTrack?.position = (position * 1000).toLong()
            logger.log("Установлена позиция: ${currentPlayer?.playingTrack?.position?.div(1000)}")
        }
    }

    fun playTrack(track: Track, position: Int, session: String) {
        logger.log("Воспроизведение трека \"${track.name}\" для ${source.player.instance.name} (${track.link})")
        stopTrack()
        val lavaTrack = track.getAudio()
        val player = AudioManager.lavaPlayerManager.createPlayer().apply {
            playTrack(lavaTrack)
            playingTrack.position = (position * 1000).toLong()
        }
        currentSessionUUID = session
        currentTrack = lavaTrack
        currentPlayer = player

        val fadeInDuration = track.fadeInTime
        val fadeOutDuration = track.fadeOutTime
        val frameProvider = MusicFrameProvider(player, fadeInDuration, fadeOutDuration,
            (player.playingTrack.duration / 1000).toDouble(),
            (player.playingTrack.position / 1000).toDouble(),
            SourceManager.voiceServer.defaultEncryption)
        sender = source.createAudioSender(frameProvider).apply { start() }
        sender.onStop { stopTrack() }
    }

    fun stopTrack() {
        currentPlayer?.destroy()
        currentTrack = null
        currentPlayer = null
    }

    fun pauseTrack() { currentPlayer?.let { it.isPaused = true } }
    fun resumeTrack() { currentPlayer?.let { it.isPaused = false } }
}
