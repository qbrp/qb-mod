package org.qbrp.client.engine.movement

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayerEntity
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.getClientPlayer
import org.qbrp.client.core.isPlayerInstantiated
import org.qbrp.client.core.onClientTick
import org.qbrp.client.core.onClientWorldTick
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.player.PlayerMovementSystem
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.ZeroWorldPos

@Autoload(env = EnvType.CLIENT)
class MovementModule : EngineModule("movement") {
    override fun getKoinModule(): Module = module {
        single { PlayerMovementSystem() }
    }

    override fun onEnable() {
        val system = get<PlayerMovementSystem>()
        val client = MinecraftClient.getInstance()

        var prevPos = ZeroWorldPos()
        var currPos = ZeroWorldPos()

        onClientWorldTick {
            prevPos = currPos
            currPos = getClientPlayer()?.pos ?: return@onClientWorldTick
            system.update(get())
        }

        WorldRenderEvents.START.register {
            val entity = client.player ?: return@register
            val tickDelta = client.tickDelta.toDouble()
            val pos = prevPos.asVec3().lerp(currPos.asVec3(), tickDelta)
            entity.setPos(pos.x, pos.y, pos.z)
        }
    }
}