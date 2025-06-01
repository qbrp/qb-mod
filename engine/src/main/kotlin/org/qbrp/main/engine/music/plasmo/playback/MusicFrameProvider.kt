package org.qbrp.main.engine.music.plasmo.playback

import su.plo.voice.api.encryption.Encryption
import su.plo.voice.api.server.audio.provider.AudioFrameProvider
import su.plo.voice.api.server.audio.provider.AudioFrameResult
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioTrackState
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler

class MusicFrameProvider(
    private val player: AudioPlayer,
    private val encryption: Encryption
) : AudioFrameProvider {

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
