package org.qbrp.client.engine.players

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.Vec3d
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.client.engine.movement.PlayerPositionLookPacketEvent
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.util.math.toMinecraft
import org.qbrp.main.engine.physics.player.isSupported
import org.qbrp.main.engine.physics.player.velocity

object LocalPlayerMixinAccess : KoinComponent {
    private val playerManager by inject<ClientPlayerManager>()


    fun getLocalPlayerVelocity(): Vec3d? {
        return playerManager.clientPlayer?.velocity?.toMinecraft()
    }

    fun isLocalPlayerOnGround(): Boolean? {
        return playerManager.clientPlayer?.isSupported
    }

    fun emitPlayerPositionLookPacketEvent(p: PlayerPositionLookS2CPacket) {
        PlayerPositionLookPacketEvent(p.x, p.y, p.z, p.pitch.toDouble(), p.yaw.toDouble()).emit()
    }
}