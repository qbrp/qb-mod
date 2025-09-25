package org.qbrp.main.engine.players.inventory.serialization

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.engine.containers.v2.serialization.ContainerDto
import org.qbrp.main.engine.players.inventory.hand.Hand

@Serializable
data class CharacterInventoryDto(val lHand: HandDto, val rHand: HandDto, val equipment: ContainerDto): DTO

@Serializable
data class HandDto(val side: Hand.Side, val ids: List<String>): DTO