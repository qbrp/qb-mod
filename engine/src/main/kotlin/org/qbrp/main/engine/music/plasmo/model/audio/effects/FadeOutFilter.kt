package org.qbrp.main.engine.music.plasmo.model.audio.effects

import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter

class FadeOutFilter(
    private val output: FloatPcmAudioFilter,
    private val fadeDurationMillis: Long,
    private val sampleRate: Int,
    private val channelCount: Int,
) : FloatPcmAudioFilter {
    var isFadingOut: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    // Если начали затухание, сбрасываем обработанные сэмплы
                    processedSamples = 0
                    isFadedOut = false // Сбрасываем флаг, если включаем эффект снова
                    println("FadeOutFilter Debug: started fading out")
                }
            }
        }

    var isFadedOut: Boolean = false
        private set

    private var processedSamples: Long = 0
    private val totalFadeSamples: Long = fadeDurationMillis * sampleRate / 1000

    override fun process(samples: Array<out FloatArray?>?, offset: Int, length: Int) {
        if (samples == null) {
            output.process(null, offset, length)
            return
        }

        if (!isFadingOut) {
            output.process(samples, offset, length)
            return
        }

        for (i in offset until offset + length) {
            val factor = if (processedSamples < totalFadeSamples) {
                1f - processedSamples.toFloat() / totalFadeSamples
            } else {
                0f
            }

            if ((processedSamples % 5000).toInt() == 0) {
                println("Out Factor: $factor | IsFading: $isFadingOut | IsFaded: $isFadedOut")
            }

            for (channel in samples) {
                channel?.let {
                    it[i] *= factor
                }
            }

            processedSamples++
        }

        // Проверяем, завершилось ли затухание
        if (processedSamples >= totalFadeSamples * 2 && !isFadedOut) {
            isFadedOut = true
            println("FadeOutFilter Debug: track fully faded out, isFadedOut set to true")
        }

        output.process(samples, offset, length)
    }

    override fun seekPerformed(seekTime: Long, provided: Long) {
        processedSamples = 0 // При seek сбрасываем отсчёт затухания
        isFadedOut = false
        output.seekPerformed(seekTime, provided)
    }

    override fun flush() {
        output.flush()
    }

    override fun close() {
        output.close()
    }
}
