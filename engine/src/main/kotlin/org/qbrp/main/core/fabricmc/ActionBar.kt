package org.qbrp.main.core.fabricmc

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.physics.player.AnyMinecraftEntities
import org.qbrp.main.engine.physics.player.MinecraftEntities
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import java.util.concurrent.ConcurrentLinkedQueue

data class ActionBarStatusComponent(
    var message: String,
    var period: Long,
    var sent: Long,
    @Volatile var lock: Boolean
) : DataComponent {
    fun update(status: ActionBarStatus) {
        message = status.message
        period = status.period
        sent = System.currentTimeMillis()
        status.lock
    }
}

data class ActionBarStatus(
    val message: String,
    val period: Long,
    val lock: Boolean
)

data class ActionBarMessageQueue(
    val messages: ConcurrentLinkedQueue<ActionBarStatus>
) : DataComponent

class ActionBarSystem {
    fun update(playerList: PlayerList, entities: AnyMinecraftEntities) {
        for (player in playerList) {
            val entity = entities.getEntity(player.playerId) ?: continue
            val actionBar = player.get<ActionBarStatusComponent>() ?: continue
            val currentTime = System.currentTimeMillis()
            val status = actionBar.message
            val isExpired = currentTime > actionBar.sent + actionBar.period && !actionBar.lock
            if (status != "" && !isExpired) {
                entity.sendMessage(actionBar.message.formatMm(), true)
                actionBar.sent = currentTime
            }

            val messageQueue = player.get<ActionBarMessageQueue>() ?: continue
            val messages = messageQueue.messages
            if (isExpired && messages.isNotEmpty()) {
                val nextStatus = messages.poll()
                actionBar.update(nextStatus)
            }
        }
    }
}

fun PlayerObject.lockActionBarStatus(text: String) {
    get<ActionBarMessageQueue>()?.messages?.add(
        ActionBarStatus(
            text,
            0L,
            true
        )
    )
}

fun PlayerObject.unlockActionBarStatus() {
    get<ActionBarStatusComponent>()?.lock = false
}