package org.qbrp.main.mechanics.tooltip

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.engine.text.map.TextMappingRegistry
import org.qbrp.main.engine.text.map.toDto
import org.qbrp.main.engine.text.map.toText
import org.qbrp.main.mechanics.tooltip.component.NameTooltipComponent
import org.qbrp.main.mechanics.tooltip.component.PlainTextTooltipComponent
import org.qbrp.main.mechanics.tooltip.component.ProgressBarTooltipComponent
import org.qbrp.main.mechanics.tooltip.component.ProgressBarTooltipComponent.ColorProfile
import org.qbrp.main.mechanics.tooltip.component.TooltipComponent

sealed interface TooltipDto

@Serializable
data class NameTooltipDto(val text: TextDto): TooltipDto

@Serializable
data class PlainTextTooltipDto(val lines: List<TextDto>): TooltipDto

@Serializable
data class ProgressBarTooltipDto(val progress: Float,
                                 val progressBarWidth: Float,
                                 val colorProfile: ColorProfile,
                                 val annotation: TextDto): TooltipDto

context(text: TextMappingRegistry)
fun mapTooltipComponentToDto(component: TooltipComponent): TooltipDto {
    return when(component) {
        is NameTooltipComponent -> NameTooltipDto(component.name.toDto())
        is PlainTextTooltipComponent -> PlainTextTooltipDto(component.lines.map { it.toDto() })
        is ProgressBarTooltipComponent -> ProgressBarTooltipDto(
            component.progress,
            component.progressBarWidth,
            component.colorProfile,
            component.annotation.toDto()
        )
        else -> throw NotImplementedError()
    }
}

context(text: TextMappingRegistry)
fun mapTooltipDtoToComponent(dto: TooltipDto): TooltipComponent {
    return when(dto) {
        is NameTooltipDto -> NameTooltipComponent(dto.text.toText())
        is PlainTextTooltipDto -> PlainTextTooltipComponent(dto.lines.map { it.toText() })
        is ProgressBarTooltipDto -> ProgressBarTooltipComponent(
            dto.progress,
            dto.progressBarWidth,
            dto.colorProfile,
            dto.annotation.toText()
        )
    }
}