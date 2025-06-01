package org.qbrp.main.engine.chat.core.system

import PermissionsUtil.hasPermission
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageComponent
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.Component
import org.qbrp.main.core.utils.networking.messages.types.IntContent
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.world.getPlayersInRadius
import kotlin.collections.set

@JsonIgnoreProperties(ignoreUnknown = true)
open class ChatGroup(
    val name: String,
    val simpleName: String = "",
    val prefix: String = "",
    val color: String = "#FFFFFFF",
    val radius: Int = 16,
    val components: List<MessageComponent>? = listOf(),
    val format: String = "{playerName}: {text}",
    val cooldown: Int = 0,
    val permission: Boolean = false,
) {
    @Transient
    private var cooldownMap: MutableMap<String, Long>? = null
    @Transient
    var buildedComponents: List<Component>? = null


    fun buildComponents() {
        buildedComponents = components?.map { it.build() } ?: emptyList()
    }

    fun cooldownPlayer(player: ServerPlayerEntity) {
        if (cooldownMap == null) { cooldownMap = HashMap() }
        cooldownMap?.set(player.name.string, System.currentTimeMillis())
    }

    fun getDefaultComponents(): List<Component> {
        return buildedComponents ?: emptyList()
    }

    fun getEstimatedCooldown(player: ServerPlayerEntity): Long = cooldownMap?.get(player.name.string) ?: 0L

    fun cooldownPassedFor(player: ServerPlayerEntity): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastCooldown = cooldownMap?.get(player.name.string) ?: 0L
        return (currentTime - lastCooldown) >= cooldown * 100
    }

    fun playerCanWrite(player: ServerPlayerEntity): Boolean = playerHasWritePermission(player)

    fun playerHasWritePermission(player: ServerPlayerEntity): Boolean = if (permission) player.hasPermission("chat.group.$name.write") else true
    fun playerHasReadPermission(player: ServerPlayerEntity): Boolean = if (permission)  player.hasPermission("chat.group.$name.read") else true

    open fun getPlayersCanSee(source: PlayerEntity): Collection<PlayerEntity> {
        return source.world.players
            .getPlayersInRadius(source, radius.toDouble(), true, true)
            .filter { playerHasReadPermission(it as ServerPlayerEntity) }
    }

    open fun isInMessage(message: ChatMessage): Boolean {
        val text = message.getText()

        // Проверяем, что текст достаточно длинный, прежде чем вызывать substring
        if (text.length < prefix.length) return false

        return text.substring(0, prefix.length) == prefix
    }


    open fun getFormattedName() = "$color$simpleName"

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
