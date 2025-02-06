package org.qbrp.engine.music.plasmo.model.audio

import java.util.Timer
import kotlin.concurrent.schedule
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.playback.MusicFrameProvider
import org.qbrp.engine.music.plasmo.playback.lavaplayer.AudioManager
import org.qbrp.engine.music.plasmo.playback.player.PlayerState
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.audio.source.AudioSender
import su.plo.voice.api.server.audio.source.ServerBroadcastSource
import su.plo.voice.api.server.player.VoiceServerPlayer
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.TrackMarker
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.div
import kotlin.text.toInt

class Radio(
    val source: ServerBroadcastSource,
    val track: Track,
    val onTrackFinished: () -> Unit = {}) {
    val repeats: Int = track.cycle
    var currentRepeat: Int = 0

    private val stopMarkHandler = object : TrackMarkerHandler {
        override fun handle(markerState: TrackMarkerHandler.MarkerState?) {
            if (markerState == TrackMarkerHandler.MarkerState.REACHED || markerState == TrackMarkerHandler.MarkerState.LATE || markerState == TrackMarkerHandler.MarkerState.ENDED) {
                if (++currentRepeat < repeats) {
                    logger.log("Трек ${track.name} проигрывается заново (${currentRepeat + 1} / $repeats))")
                    repeat()
                } else {
                    onTrackFinished()
                }
            }
        }
    }

    val audioTrack = track.getAudio()
    val audioPlayer = AudioManager.lavaPlayerManager.createPlayer().apply {
        playTrack(audioTrack)
        playingTrack.addMarker(TrackMarker((track.endTimestamp * 1000).toLong() - 800, stopMarkHandler))
    }

    val frameProvider = MusicFrameProvider(audioPlayer, Engine.musicManagerModule.getVoiceServer().defaultEncryption)
    val bufferedSender: AudioSender = source.createAudioSender(frameProvider).apply { start() }

    fun repeat() {
        audioPlayer.playTrack(audioTrack?.makeClone())
        audioPlayer.playingTrack.position = 0
        audioPlayer.playingTrack.addMarker(TrackMarker((track.endTimestamp * 1000).toLong() - 800, stopMarkHandler))
    }

    fun getCurrentPosition() = try {
        (audioPlayer!!.playingTrack?.position?.div(1000))?.toInt()
    } catch (e: IllegalArgumentException) { 0;}

    fun stop() {
        logger.log("Трек остановлен: ${track.name}")
        audioPlayer.isPaused = true
        bufferedSender.pause()
    }

    fun resume() {
        logger.log("Трек возобновлен: ${track.name}")
        audioPlayer.isPaused = false
        bufferedSender.resume()
    }

    fun destroy() {
        bufferedSender.stop()
    }

    companion object {
        val logger = Loggers.get("musicManager", "controller")
    }
}
