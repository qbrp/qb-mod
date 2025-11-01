package org.qbrp.main.engine.players.inventory.serialization

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.engine.containers.serialization.ContainerDto

@Serializable
data class CharacterInventoryDto(val lHand: ContainerDto, val rHand: ContainerDto, val equipment: ContainerDto): DTO