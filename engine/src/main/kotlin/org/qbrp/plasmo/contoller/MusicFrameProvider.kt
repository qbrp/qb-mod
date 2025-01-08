package org.qbrp.plasmo.contoller

import su.plo.voice.api.encryption.Encryption
import su.plo.voice.api.server.audio.provider.AudioFrameProvider
import su.plo.voice.api.server.audio.provider.AudioFrameResult
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioTrackState

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MusicFrameProvider(
    private val player: AudioPlayer,
    private val fadeInTime: Double,
    private val fadeOutTime: Double,
    private val trackDuration: Double,
    private val startOffset: Double,
    private val encryption: Encryption
) : AudioFrameProvider {

    private var currentVolume: Double = 0.0
    private var lastVolumeUpdateTime: Double = 0.0
    private var startTime: Long = -1L // Используем Long для nanoTime

    override fun provide20ms(): AudioFrameResult {
        val currentTrack = player.playingTrack
        if (currentTrack == null || currentTrack.state == AudioTrackState.FINISHED) {
            return AudioFrameResult.Finished
        }
        val frameData = player.provide()?.data?.let {
            encryption.encrypt(it)
        }
        return AudioFrameResult.Provided(frameData)
    }
}
