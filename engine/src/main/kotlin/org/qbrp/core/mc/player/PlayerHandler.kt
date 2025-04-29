package org.qbrp.core.mc.player

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageReceivedEvent

class PlayerHandler(private val session: PlayerObject) {

    companion object {
        init {
            // Обработка тика
            ServerTickEvents.END_WORLD_TICK.register { server ->
                PlayerManager.playersList.forEach { player ->
                    player.handler.handleTick()
                }
            }
        }
    }

    private fun createAttributeModifierId(type: String = "modifier") = "${session.entity.name.string}-$type}"

    private fun createSpeedAttributeModifier(speed: Int): EntityAttributeModifier {
        return EntityAttributeModifier(
            createAttributeModifierId("genericSpeed"),
            speed / 10.toDouble(),
            EntityAttributeModifier.Operation.MULTIPLY_BASE
        )
    }

    fun handleTick() {
        updateSpeed()
    }

    var speedAttributeModifier: EntityAttributeModifier = createSpeedAttributeModifier(session.speed ?: PlayerManager.getDefaultSpeed(session.entity.interactionManager.gameMode))
    var cachedSpeed: Int = session.speed ?: PlayerManager.getDefaultSpeed(session.entity.interactionManager.gameMode)

    private fun updateSpeed() {
        val currentSpeed = session.speed ?: PlayerManager.getDefaultSpeed(session.entity.interactionManager.gameMode)
        if (cachedSpeed != currentSpeed) {
            speedAttributeModifier = createSpeedAttributeModifier(currentSpeed)
            val speedAttribute = session.entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            removeModifiers()
            speedAttribute?.addPersistentModifier(speedAttributeModifier)
            cachedSpeed = currentSpeed
        }
    }

    private fun removeModifiers() {
        val attributeInstance = session.entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED) ?: return
        attributeInstance.modifiers.forEach { modifier ->
            attributeInstance.removeModifier(modifier.id)
        }
    }
}