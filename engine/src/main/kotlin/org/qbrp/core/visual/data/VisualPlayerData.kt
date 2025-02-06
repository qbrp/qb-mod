package org.qbrp.core.visual.data

import net.minecraft.entity.player.PlayerEntity
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.types.BooleanContent
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.StringContent
import java.util.UUID

class VisualPlayerData(val player: PlayerEntity, uuid: String = UUID.randomUUID().toString())
    : VisualData(player.world, uuid, player.blockX, player.blockY, player.blockZ) {
    override val x: Int
        get() = player.blockX
    override val y: Int
        get() = player.blockY
    override val z: Int
        get() = player.blockZ
    private val playerUUID: UUID = player.uuid
    var isWriting: Boolean = false

    override fun toString(): String {
        return "VisualPlayerData(playerUUID=$playerUUID, x=${x}, y=${y}, z=${z}, isWriting=$isWriting)"
    }

    override fun toCluster(): Cluster {
        return ClusterBuilder()
            .header("visualType", StringContent("player"))
            .component("uuid", StringContent(uuid))
            .component("nickname", StringContent(player.name.string))
            .component("isWriting", BooleanContent(isWriting))
            .build()
    }
}
