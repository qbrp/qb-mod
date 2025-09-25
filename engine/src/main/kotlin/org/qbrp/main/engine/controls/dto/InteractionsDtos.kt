package org.qbrp.main.engine.controls.dto

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.controls.entity.display.InteractionAvailability
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.mechanics.tooltip.TooltipDto

@Serializable
data class InteractionContextMenuViewDto(val entries: List<Entry>) {
    @Serializable
    class Entry(val obj: InteractableViewDTO, val nodes: List<InteractionViewNodeDto>)
}

@Serializable
data class InteractionViewNodeDto(
    val identity: InteractionIdentityDto?,
    val icon: InteractableIconDTO?,
    val name: TextDto,
    val description: List<TooltipDto>,
    val nodes: List<InteractionViewNodeDto>
)

@Serializable
data class InteractionIdentityDto(val id: Long, val king: String, val failures: List<String>)

@Serializable
data class InteractableViewDTO(val name: TextDto, val icon: InteractableIconDTO)

@Serializable
sealed class InteractableIconDTO() {
    @Serializable class Asset(val id: String): InteractableIconDTO()
    @Serializable class Player(val id: String): InteractableIconDTO()
    @Serializable class Item(val id: String): InteractableIconDTO()
}