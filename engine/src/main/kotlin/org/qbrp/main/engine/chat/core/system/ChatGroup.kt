package org.qbrp.main.engine.chat.core.system

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.core.fabricmc.player.hasPermission
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageComponent
import org.qbrp.main.core.util.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.util.networking.messages.components.Cluster
import org.qbrp.main.core.util.networking.messages.components.Component
import org.qbrp.main.core.util.networking.messages.types.IntContent
import org.qbrp.main.core.util.networking.messages.types.StringContent
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.engine
import org.qbrp.main.engine.players.model.engineOrNull
import org.qbrp.main.engine.players.model.service.PlayersUtil.getPlayersInRadius
import org.qbrp.main.core.util.PlayerId
import kotlin.collections.set

@JsonIgnoreProperties(ignoreUnknown = true)
open class ChatGroup(
    val name: String,
    val simpleName: String = "",
    val prefix: String = "",
    val color: String = "#FFFFFFF",
    val radius: Int = 16,
    val components: List<MessageComponent>? = listOf(),
    val format: String = "{playerName}: {chatText}",
    val cooldown: Int = 0,
    val permission: Boolean = false,
) {
    @Transient
    private var cooldownMap: MutableMap<PlayerId, Long>? = null
    @Transient
    var buildedComponents: List<Component>? = null

    fun buildComponents() {
        buildedComponents = components?.map { it.build() } ?: emptyList()
    }

    fun cooldownPlayer(player: ServerPlayerObject) {
        if (cooldownMap == null) { cooldownMap = HashMap() }
        cooldownMap?.set(player.playerId, System.currentTimeMillis())
    }

    fun getDefaultComponents(): List<Component> {
        return buildedComponents ?: emptyList()
    }

    fun getEstimatedCooldown(player: ServerPlayerObject): Long = cooldownMap?.get(player.playerId) ?: 0L

    fun cooldownPassedFor(player: ServerPlayerObject): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastCooldown = cooldownMap?.get(player.playerId) ?: 0L
        return (currentTime - lastCooldown) >= cooldown * 100
    }

    fun playerCanWrite(player: ServerPlayerObject): Boolean = playerHasWritePermission(player)

    fun playerHasWritePermission(player: ServerPlayerObject): Boolean = if (permission) player.hasPermission(Permission("chat.group.$name.write")) else true
    fun playerHasReadPermission(player: ServerPlayerObject): Boolean = if (permission)  player.hasPermission(Permission("chat.group.$name.read")) else true

    open fun getPlayersCanSee(source: ServerPlayerEntity): Collection<ServerPlayerObject> {
        return listOf(source.engine()) + getPlayersInRadius(source, radius.toDouble())
            .mapNotNull { it.engineOrNull() }
            .filter { playerHasReadPermission(it) }
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
            .component("label", StringContent(name))
            .component("simpleName", StringContent(simpleName))
            .component("prefix", StringContent(prefix))
            .component("color", StringContent(color))
            .component("radius", IntContent(radius))
            .build()
    }
}
