package org.qbrp.core.game.player

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.qbrp.core.game.player.events.PlayerChangeGameModeEvent
import org.qbrp.core.game.player.registration.PlayerRegistrationCallback
import org.qbrp.engine.chat.core.events.MessageReceivedEvent

class PlayerHandler(private val session: ServerPlayerSession) {

    init {
        // Блокировка смена режима игры, если игрок не авторизован
        PlayerChangeGameModeEvent.EVENT.register() { player, gameMode ->
            if (player.name.string == session.entity.name.string && !session.isAuthorized() && gameMode != GameMode.SPECTATOR) {
                ActionResult.PASS
            } else
                ActionResult.PASS
        }

        // Блокировка отправки сообщений
        MessageReceivedEvent.EVENT.register() { message ->
            if (message.getAuthorEntity() == session.entity && !session.isAuthorized()) {
                ActionResult.FAIL
            } else
                ActionResult.PASS
        }

        // Обработка тика
        ServerTickEvents.END_WORLD_TICK.register { server ->
            handleTick()
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