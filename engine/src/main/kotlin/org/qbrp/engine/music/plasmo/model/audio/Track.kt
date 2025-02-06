package org.qbrp.engine.music.plasmo.model.audio

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.qbrp.engine.music.plasmo.model.audio.Radio.Companion.logger
import org.qbrp.engine.music.plasmo.playback.lavaplayer.AudioManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioTrack

@JsonIgnoreProperties(ignoreUnknown = true)
class Track(val link: String, val name: String, var cycle: Int) {
    var startTimestamp: Double = 0.0
    var endTimestamp: Double = 0.0

    @JsonIgnore
    fun getAudio(): AudioTrack? {
        return try {
            AudioManager.getTrack(link).join()
        } catch (e: Exception) {
            null.also {
                logger.error("Источник трека $name по $link не найден: ${e.message}")
            }
        }
    }
    @JsonIgnore
    fun setEndFromAudio() { endTimestamp = getAudio()?.duration?.toDouble()?.div(1000) ?: 0.0 }

}
