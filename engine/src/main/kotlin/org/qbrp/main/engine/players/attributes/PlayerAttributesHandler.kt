package org.qbrp.main.engine.players.attributes

import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.mc.player.service.PlayerBehaviour

class PlayerAttributesHandler: PlayerBehaviour(), Tick<ServerWorld> {
    override val save = false

    private fun createAttributeModifierId(type: String = "modifier") = "${player.entity.name.string}-$type}"

    private fun createSpeedAttributeModifier(speed: Int): EntityAttributeModifier {
        return EntityAttributeModifier(
            createAttributeModifierId("genericSpeed"),
            speed / 10.toDouble(),
            EntityAttributeModifier.Operation.MULTIPLY_BASE
        )
    }

    private lateinit var speedAttributeModifier: EntityAttributeModifier
    private var cachedSpeed: Int = 1

    override fun onEnable() {
        speedAttributeModifier = createSpeedAttributeModifier(getSpeed())
        cachedSpeed = getSpeed()
    }

    private fun updateSpeed() {
        val currentSpeed = getSpeed()
        if (cachedSpeed != currentSpeed) {
            speedAttributeModifier = createSpeedAttributeModifier(currentSpeed)
            val speedAttribute = player.entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            removeModifiers()
            speedAttribute?.addPersistentModifier(speedAttributeModifier)
            cachedSpeed = currentSpeed
        }
    }

    private fun removeModifiers() {
        val attributeInstance = player.entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED) ?: return
        attributeInstance.modifiers.forEach { modifier ->
            attributeInstance.removeModifier(modifier.id)
        }
    }

    private fun getSpeed() = player.getComponent<PlayerAttributes>()!!.getSpeed()

    override fun tick(context: ServerWorld) {
        updateSpeed()
    }

}