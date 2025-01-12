package org.qbrp.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.qbrp.plasmo.playback.lavaplayer.AudioManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
class Track(val link: String,
            val name: String,
            val cycle: Int) {

    val startTimestamp: Double = 0.0
    var endTimestamp: Double = 0.0

    init { setEnd() }

    @JsonIgnore
    fun getAudio(): AudioTrack {
        return AudioManager.getTrack(link).join()
    }
    fun setEnd() { endTimestamp = getAudio().duration.toDouble()/1000 }
}