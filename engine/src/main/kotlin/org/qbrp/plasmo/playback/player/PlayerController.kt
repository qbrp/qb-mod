package org.qbrp.plasmo.playback.player

import org.qbrp.plasmo.playback.MusicFrameProvider
import org.qbrp.plasmo.playback.lavaplayer.AudioManager
import org.qbrp.plasmo.model.audio.Playlist
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

    private var currentSessionUUID: String = "unset"
    private var currentTrack: AudioTrack? = null
    private var currentPlayer: AudioPlayer? = null
    private lateinit var sender: AudioSender

    fun sync(playback: Playlist.Playback) {
        val track = playback.getCurrentTrack()
        val session = playback.playSession
        val position = playback.currentTime
        var currentPosition = (currentPlayer?.playingTrack?.position ?: 0) / 1000
        if (playback.playJob == null) { stopTrack(); return }
        val isTrackChanged = session != currentSessionUUID
        if (isTrackChanged) {
            logger.log("Рассинхронизирован ${track.name}: UUID $currentSessionUUID слушателя не совпадает с $session")
            playTrack(track, playback)
            currentSessionUUID = session
            return
        }
        val isPositionChanged = abs(position - currentPosition) > 6
        if (isPositionChanged) {
            logger.log("Рассинхронизирован ${track.name}: текущая позиция ${currentPosition}, настоящая $position")
            currentPlayer?.playingTrack?.position = position.toLong() * 1000
            logger.log("Установлена позиция: $currentPosition")
        }
    }

    fun playTrack(track: Track, playback: Playlist.Playback) {
        logger.log("Воспроизведение трека \"${track.name}\" для ${source.player.instance.name} (${track.link})")
        stopTrack()
        currentTrack = track.getAudio()
        currentPlayer = AudioManager.lavaPlayerManager.createPlayer()?.apply {
            playTrack(currentTrack)
            playingTrack.position = (playback.currentTime * 1000).toLong()
        } ?: throw IllegalStateException("Player creation failed")

        val frameProvider = MusicFrameProvider(currentPlayer as AudioPlayer,
            (currentPlayer!!.playingTrack.duration / 1000).toDouble(),
            (currentPlayer!!.playingTrack.position / 1000).toDouble(),
            SourceManager.voiceServer.defaultEncryption)
        sender = source.createAudioSender(frameProvider).apply { start() }
        sender.onStop { stopTrack() }
    }

    fun stopTrack() {
        currentSessionUUID = ""
        currentPlayer?.destroy()
        currentTrack = null
        currentPlayer = null
    }

    fun pauseTrack() { currentPlayer?.let { it.isPaused = true } }
    fun resumeTrack() { currentPlayer?.let { it.isPaused = false } }
}
