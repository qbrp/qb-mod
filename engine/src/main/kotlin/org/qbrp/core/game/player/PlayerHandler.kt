package org.qbrp.core.game.player

import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes

class PlayerHandler(private val session: ServerPlayerSession) {

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