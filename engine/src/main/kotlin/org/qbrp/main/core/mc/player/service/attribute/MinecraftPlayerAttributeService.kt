package org.qbrp.main.core.mc.player.service.attribute

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.attribute.AdditionModifier
import org.qbrp.main.core.mc.player.attribute.McAttribute

interface MinecraftPlayerAttributeService {
    fun getSpeedModifier(entity: ServerPlayerEntity): AdditionModifier
    fun updateSpeedModifier(entity: ServerPlayerEntity, value: Double)
}