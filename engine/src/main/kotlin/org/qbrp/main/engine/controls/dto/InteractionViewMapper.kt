package org.qbrp.main.engine.controls.dto
import org.qbrp.main.engine.controls.entity.display.IconItemModelId
import org.qbrp.main.engine.controls.entity.display.IconTextureId
import org.qbrp.main.engine.controls.entity.display.old.InteractActivationViewData
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractableName
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.display.InteractionAvailability
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.controls.entity.display.InteractionIdentity
import org.qbrp.main.engine.controls.entity.display.InteractionViewNode
import org.qbrp.main.engine.controls.entity.display.old.InteractionViewData
import org.qbrp.main.engine.controls.entity.interactions.InteractionKind
import org.qbrp.main.engine.controls.entity.interactions.toInteractionId
import org.qbrp.main.engine.players.model.util.PlayerId
import org.qbrp.main.engine.text.map.TextMappingRegistry
import org.qbrp.main.engine.text.map.toDto
import org.qbrp.main.engine.text.map.toText
import org.qbrp.main.mechanics.tooltip.mapTooltipComponentToDto
import org.qbrp.main.mechanics.tooltip.mapTooltipDtoToComponent

class InteractionViewMapper(private val textDtoMapper: TextMappingRegistry) {
    fun contextMenuViewToDto(menuViewData: InteractionContextMenuViewData): InteractionContextMenuViewDto {
        return InteractionContextMenuViewDto(
            menuViewData.objectInteractions
                .map {
                    InteractionContextMenuViewDto.Entry(
                    mapInteractableViewToDto(it.obj),
                    it.nodes.map { interactionViewNodeToDto(it) }
                )
            }
        )
    }

    fun contextMenuDtoToView(menuViewDto: InteractionContextMenuViewDto): InteractionContextMenuViewData {
        return InteractionContextMenuViewData(
            menuViewDto.entries.map {
                InteractionContextMenuViewData.Entry(
                    mapInteractableDtoToView(it.obj),
                    it.nodes.map { interactionNodeDtoToViewData(it) }
                )
            }
        )
    }

    fun interactionViewNodeToDto(node: InteractionViewNode): InteractionViewNodeDto = with(textDtoMapper) {
        return InteractionViewNodeDto(
            node.interaction?.let { interactionIdentityToDto(it) },
            node.icon?.let { iconToDto(it) },
            node.label.toDto(),
            node.description.map { mapTooltipComponentToDto(it) },
            node.children.map { interactionViewNodeToDto(it) },
        )
    }


    fun interactionNodeDtoToViewData(dto: InteractionViewNodeDto): InteractionViewNode = with(textDtoMapper) {
        return InteractionViewNode(
            dto.identity?.let { interactionIdentityFromDto(it) },
            dto.name.toText(),
            dto.icon?.let { iconFromDto(it) },
            dto.description.map { mapTooltipDtoToComponent(it) },
            dto.nodes.map { interactionNodeDtoToViewData(it) },
        )
    }

    fun interactionIdentityToDto(identity: InteractionIdentity): InteractionIdentityDto {
        val failures = when(identity.availability) {
            is InteractionAvailability.No -> identity.availability.reasons
            InteractionAvailability.Yes -> listOf()
        }
        return InteractionIdentityDto(identity.id.value, identity.kind.name, failures)
    }

    fun interactionIdentityFromDto(identity: InteractionIdentityDto): InteractionIdentity {
        val availability = if (identity.failures.isNotEmpty()) {
            InteractionAvailability.No(identity.failures)
        } else {
            InteractionAvailability.Yes
        }
        return InteractionIdentity(identity.id.toInteractionId(), InteractionKind(identity.king), availability)
    }

    private fun mapInteractableViewToDto(view: InteractableViewData): InteractableViewDTO = view.let {
        InteractableViewDTO(
            textDtoMapper.toDto(it.name.text),
            iconToDto(it.icon)
        )
    }

    private fun mapInteractableDtoToView(dto: InteractableViewDTO): InteractableViewData = dto.let {
        InteractableViewData(
            InteractableName(textDtoMapper.fromDto(it.name)),
            iconFromDto(it.icon)
        )
    }

    private fun iconFromDto(dto: InteractableIconDTO): InteractionIcon {
        return when (dto) {
            is InteractableIconDTO.Item -> InteractionIcon.Item(IconItemModelId(dto.id))
            is InteractableIconDTO.Asset -> InteractionIcon.Asset(IconTextureId(dto.id))
            is InteractableIconDTO.Player -> InteractionIcon.Player(PlayerId(dto.id))
        }
    }

    private fun iconToDto(icon: InteractionIcon): InteractableIconDTO {
        return when (icon) {
            is InteractionIcon.Item -> InteractableIconDTO.Item(icon.id.value)
            is InteractionIcon.Asset -> InteractableIconDTO.Asset(icon.id.value)
            is InteractionIcon.Player -> InteractableIconDTO.Player(icon.id.value)
        }
    }
}