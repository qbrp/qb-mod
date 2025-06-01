package org.qbrp.main.engine.music.plasmo.model.audio.effects

import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter

class ConditionalFadeInFilter(
    output: FloatPcmAudioFilter,
    fadeDurationMillis: Long,
    sampleRate: Int,
    channelCount: Int,
    private val shouldChangeVolume: () -> Boolean
) : FadeInFilter(output, fadeDurationMillis, sampleRate, channelCount) {

    override fun process(samples: Array<out FloatArray?>?, offset: Int, length: Int) {
        if (shouldChangeVolume()) {
            super.process(samples, offset, length)
        } else {
            processedSamples = totalFadeSamples
            output.process(samples, offset, length)
        }
    }
}