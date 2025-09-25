package org.qbrp.client.engine.inventory.space

import net.minecraft.text.Text
import org.qbrp.main.core.utils.format.Format.formatMm
import org.qbrp.main.deprecated.SpacePacketDto

class SpaceInformation: SpaceInfoProvider {
    var info: String = ""
    var fullness: Float = 0f

    override fun getTooltip(): List<Text> {
        return (listOf("Информация") + info.split("<newline>")).map { it.formatMm() }
    }

    fun updateValues(dto: SpacePacketDto) {
        info = dto.info
        fullness = dto.fulness.toFloat()
    }

    var progressBarValue = 0f
    override fun getProgressBarPercent(): Float {
        progressBarValue = lerp(progressBarValue, fullness, 0.5f)
        return progressBarValue
    }

    private fun lerp(start: Float, stop: Float, fraction: Float): Float {
        return (1 - fraction) * start + fraction * stop
    }
}