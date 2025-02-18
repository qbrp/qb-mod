package org.qbrp.engine.music.plasmo.model.audio.playback

import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.model.audio.Track
import org.qbrp.engine.music.plasmo.model.audio.effects.ConditionalFadeInFilter
import org.qbrp.engine.music.plasmo.model.audio.effects.FadeInFilter
import org.qbrp.engine.music.plasmo.model.audio.effects.FadeOutFilter
import org.qbrp.engine.music.plasmo.playback.MusicFrameProvider
import org.qbrp.engine.music.plasmo.playback.lavaplayer.AudioManager
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.api.server.audio.source.AudioSender
import su.plo.voice.api.server.audio.source.ServerDirectSource
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.TrackMarker
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler

class Radio(
    val source: ServerDirectSource,
    val track: Track,
    val onTrackFinished: () -> Unit = {}) {
    val repeats: Int = track.loops
    var currentRepeat: Int = 0

    val isPlaying: Boolean
        get() = !audioPlayer.isPaused

    fun fadeOut() { fadeOutFilter.isFadingOut = true }
    fun cancelFadeOut() { fadeOutFilter.isFadingOut = false }
    fun isFadedOut() = fadeOutFilter.isFadedOut

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

    private lateinit var fadeInFilter: ConditionalFadeInFilter
    private lateinit var fadeOutFilter: FadeOutFilter

    val audioTrack = track.getAudio()
    val audioPlayer = AudioManager.lavaPlayerManager.createPlayer().apply {
        setFilterFactory { _, format, output ->
            fadeInFilter = ConditionalFadeInFilter(
                output as FloatPcmAudioFilter,
                5000,
                format.sampleRate,
                format.channelCount
            ) { !fadeOutFilter.isFadingOut }

            fadeOutFilter = FadeOutFilter(
                fadeInFilter,
                5000,
                format.sampleRate,
                format.channelCount
            )
            listOf(fadeOutFilter, fadeInFilter)
        }
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
