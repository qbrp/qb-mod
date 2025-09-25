package org.qbrp.main.engine.controls.system.lookup

import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableObject
import org.qbrp.main.engine.controls.entity.interactable.InteractableSubject
import org.qbrp.main.engine.controls.system.asObject
import org.qbrp.main.engine.controls.system.asSubject
import org.qbrp.main.engine.controls.entity.interactions.AnyInteraction
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.model.inspect.InspectionInteractions
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.controls.model.player.PropInteractions

abstract class PlayerLookup(protected val player: ServerPlayerObject, val self: Boolean = false): InteractionLookup {
    protected val objects: MutableList<InteractableObject> = mutableListOf()
    protected val subjects: MutableList<InteractableSubject> = mutableListOf()
    protected val interactions: MutableList<AnyInteraction> = mutableListOf()

    open fun lookup(player: PlayerInteractable, interactionService: InteractionService) {
        val pendingInteractables = mutableListOf<Interactable>()

        val character = interactionService.getCharacterInteractable(this.player)

        interactions.add(InspectionInteractions.CHARACTER)

        if (character != null) {
            val carrier = character.carrier
            //Добавляем в качестве неопределенных участников самого игрока и средство переноса.
            //В разных ситуациях они могут быть субъектами или объектами
            pendingInteractables.apply {
                add(player)
                add(carrier)
                add(character)
                // Разные руки игрока, переноски предметов
                addAll(carrier.varianceInteractables)
            }

            when (self) {
                true -> {
                    objects.addAll(pendingInteractables.map { it.asObject() })
                }

                false -> {
                    subjects.addAll(pendingInteractables.map { it.asSubject() })
                }
            }

            //Базовые взаимодействия
            interactions.apply {
                add(PropInteractions.SPAWN_PROP)
            }
        }
    }

    override fun getAnyInteractions(): List<AnyInteraction> {
        return interactions
    }

    override fun getInteractableSubjects(): List<InteractableSubject> {
        return subjects
    }

    override fun getInteractableObjects(): List<InteractableObject> {
        return objects
    }
}