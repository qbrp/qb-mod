package org.qbrp.main.core.mc.player.service

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.prefabs.RuntimePrefab
import org.qbrp.main.core.game.serialization.ContextualFactory
import org.qbrp.main.core.game.serialization.GameMapper

import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.core.synchronization.components.MessagingChannelSender

class PlayerSerializer(val accounts: AccountDatabaseService,
                       val playerPrefab: RuntimePrefab.Tag,
                       val channel: MessagingChannelSender): ContextualFactory<ServerPlayerObject, String> {
    override fun fromJson(json: String, nickname: String): ServerPlayerObject {
        val jsonField = GameMapper.COMPONENTS_JSON.decodeFromString<PlayerJsonField>(json)
        val entity = PlayersUtil.getPlayer(nickname)
            ?: throw NullPointerException("Игрок с ником ${jsonField.name} не найден на сервере")
        jsonField.name = nickname
        val account = runBlocking { accounts.get(jsonField.accountUuid) }
        return ServerPlayerObject(entity, account!!, jsonField.state, channel)
    }

    override fun toJson(plr: ServerPlayerObject): String {
        return GameMapper.COMPONENTS_JSON.encodeToString(
            PlayerJsonField(plr.account.uuid, plr.state, plr.entityName)
        )
    }

    suspend fun newInstance(player: ServerPlayerEntity, accountUuid: String): ServerPlayerObject {
        val playerObject = ServerPlayerObject(player, accounts.get(accountUuid)!!, State(), channel)
        playerPrefab.put(playerObject)
        return playerObject
    }
}