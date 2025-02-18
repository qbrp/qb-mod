package org.qbrp.engine.chat.core.system

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.game.permissions.PermissionManager
import org.qbrp.core.game.permissions.PermissionManager.hasPermission
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.IntContent
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.utils.world.getPlayersInRadius

data class ChatGroup(
    val name: String,
    val simpleName: String = "",
    val prefix: String = "",
    val color: String = "",
    val radius: Int = 16,
    val components: List<String> = listOf(),
    val format: String = "{playerName}: {text}",
    val cooldown: Int = 0
) {
    private val cooldownMap = mutableMapOf<String, Int>()
    private fun getWritePerm() = PermissionManager.getOrRegister("chat.group.$name", "write")
    private fun getReadPerm() = PermissionManager.getOrRegister("chat.group.$name", "read")

    fun playerHasWritePermission(player: ServerPlayerEntity): Boolean = player.hasPermission(getWritePerm())
    fun playerHasReadPermission(player: ServerPlayerEntity): Boolean = player.hasPermission(getReadPerm())

    fun getPlayersCanSee(source: PlayerEntity): List<PlayerEntity> {
        return source.world.players
            .getPlayersInRadius(source, radius.toDouble(), true, true)
            .filter { playerHasReadPermission(it as ServerPlayerEntity) }
    }

    fun getFormattedName() = "$color$simpleName"

    fun toCluster(): Cluster {
        return ClusterBuilder()
            .component("name", StringContent(name))
            .component("simpleName", StringContent(simpleName))
            .component("prefix", StringContent(prefix))
            .component("color", StringContent(color))
            .component("radius", IntContent(radius))
            .build()
    }
}
