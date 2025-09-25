package org.qbrp.main.core.mc.player.service.attribute

import net.minecraft.entity.attribute.EntityAttributeInstance
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.attribute.AdditionModifier
import org.qbrp.main.core.mc.player.attribute.McAttribute
import org.qbrp.main.core.mc.player.attribute.McAttributeState
import org.qbrp.main.core.mc.player.attribute.PlayerAttributes
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.exception.AttributeInstanceNotExistsException
import org.qbrp.main.core.mc.player.toPlayerMc
import org.qbrp.main.core.mc.server.thread.ServerThread
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class MinecraftPlayerAttributeServiceImpl(private val serverThread: ServerThread): MinecraftPlayerAttributeService {
    private val playerAttributes: ConcurrentHashMap<MinecraftPlayerId, PlayerAttributes> = ConcurrentHashMap()
    private val modifierFactory = ModifierFactory()

    fun instantiateDefaultAttributeStates(entity: ServerPlayerEntity, modifier: Double) {
        val instance = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        if (instance == null) throw AttributeInstanceNotExistsException(entity, "movement speed")
        val speedModifier = modifierFactory.speed(modifier)
        instance.addPersistentModifier(speedModifier)
        val speedState = McAttributeState(AtomicReference(AdditionModifier(modifier)), speedModifier.id)
        playerAttributes[entity.uuid.toPlayerMc()] = PlayerAttributes(mapOf(McAttribute.SPEED to speedState))
    }

    override fun getSpeedModifier(entity: ServerPlayerEntity): AdditionModifier {
        val id = MinecraftPlayerId(entity.uuid)
        val speedAttr = playerAttributes[id]!!.getAttr(McAttribute.SPEED).addition
        val speedModifier = speedAttr.get().value
        return AdditionModifier(speedModifier)
    }

    override fun updateSpeedModifier(entity: ServerPlayerEntity, value: Double) {
        serverThread.assertOnServerThread()
        val instance = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        if (instance == null) throw AttributeInstanceNotExistsException(entity, "movement speed")
        val id = MinecraftPlayerId(entity.uuid)
        val speedAttr = playerAttributes[id]!!.getAttr(McAttribute.SPEED)
        val modifier = modifierFactory.speed(value)
        instance.removeModifier(speedAttr.modifierId)
        instance.addPersistentModifier(modifier)
        speedAttr.addition.set(AdditionModifier(value))
        speedAttr.modifierId = modifier.id
    }

    inner class ModifierFactory() {
        fun speed(value: Double): EntityAttributeModifier {
            return EntityAttributeModifier(
                UUID.randomUUID(),
                "engine_speed_modifier",
                value,
                EntityAttributeModifier.Operation.ADDITION
            );
        }
    }
}