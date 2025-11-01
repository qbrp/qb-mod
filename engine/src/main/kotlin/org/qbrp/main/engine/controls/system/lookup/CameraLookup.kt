package org.qbrp.main.engine.controls.system.lookup

import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.controls.system.asObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.RaycastFilter
import org.qbrp.main.engine.world.WorldRaycastManager
import org.qbrp.main.engine.world.location.asWorld

// Игрок + рука + предметы в руке > сущность перед
class CameraLookup(
    player: ServerPlayerObject,
    private val raycastManager: WorldRaycastManager,
    val distance: Double = 5.0
): PlayerLookup(player) {
    override fun lookup(player: PlayerInteractable, interactionService: InteractionService) {
        super.lookup(player, interactionService)
        val start = player.eyePos.asVec3()
        val eyeVec = player.rotationVector.normalize()
        val end = start.add(eyeVec.mul(distance))
        val raycastResult = raycastManager.raycast(start, end.asWorld(), RaycastFilter.ALL)
        val lookPlayer = raycastResult.nearestPlayer
        val lookProp = raycastResult.nearestProp

        // Добавляем игрока, на которого мы смотрим, как объект
        lookPlayer?.let {
            val interactable = interactionService.getPlayerInteractable(it)
            objects.add(interactable.asObject())
            val character = interactionService.getCharacterInteractable(it)
            character?.let { objects.add(character.asObject()) }
        }

        // Добавляем проп, на который мы смотрим, как объект
        lookProp?.serverItem?.let {
            val interactable = interactionService.getItemInteractable(it)
            objects.add(interactable.asObject())
        }
    }
}