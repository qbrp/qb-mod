package org.qbrp.main.engine.music.plasmo.model.audio.effects

import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter

open class FadeInFilter(
    protected val output: FloatPcmAudioFilter,
    protected val fadeDurationMillis: Long,
    protected val sampleRate: Int,
    private val channelCount: Int
) : FloatPcmAudioFilter {
    var isFadingIn: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    // Если начали затухание, сбрасываем обработанные сэмплы
                    processedSamples = 0
                    isFadedIn = false // Сбрасываем флаг, если включаем эффект снова
                }
            }
        }

    var isFadedIn: Boolean = false
        private set

    protected var processedSamples: Long = 0
    protected val totalFadeSamples: Long = fadeDurationMillis * sampleRate / 1000

    override fun process(samples: Array<out FloatArray?>?, offset: Int, length: Int) {
        if (samples == null) {
            output.process(null, offset, length)
            return
        }

        if (!isFadingIn) {
            output.process(samples, offset, length)
            return
        }

        for (i in offset until offset + length) {
            val factor = if (processedSamples < totalFadeSamples) {
                processedSamples.toFloat() / totalFadeSamples
            } else {
                1f
            }

            for (channel in samples) {
                channel?.let {
                    it[i] *= factor
                }
            }

            processedSamples++
        }

        // Проверяем, завершилось ли усиление громкости
        if (processedSamples >= totalFadeSamples && !isFadedIn) {
            isFadedIn = true
        }

        output.process(samples, offset, length)
    }

    override fun seekPerformed(seekTime: Long, provided: Long) {
        processedSamples = 0 // При seek сбрасываем отсчёт усиления
        isFadedIn = false
        output.seekPerformed(seekTime, provided)
    }

    override fun flush() {
        output.flush()
    }

    override fun close() {
        output.close()
    }
}
