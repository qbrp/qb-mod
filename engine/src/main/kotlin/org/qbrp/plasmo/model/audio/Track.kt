package org.qbrp.plasmo.model.audio

import org.qbrp.plasmo.contoller.lavaplayer.AudioManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.UUID

class Track(val link: String,
            val name: String,
            val cycle: Int) {
    val uuid = UUID.randomUUID().toString()
    val startTimestamp: Double = 0.0
    var endTimestamp: Double = 0.0
    var fadeInTime: Double = 0.0
    var fadeOutTime: Double = 0.0
    init { setEnd() }

    private var audioTrack: AudioTrack? = null
    fun getAudio(): AudioTrack {
        return AudioManager.getTrack(link).join()
    }
    fun setEnd() { endTimestamp = getAudio().duration.toDouble()/1000 }
    fun setFadeTime(time: Double) { fadeOutTime = time; fadeInTime = time }
}