package org.qbrp.main.core.mc.player.service

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.prefabs.RuntimePrefab
import org.qbrp.main.core.game.serialization.ContextualFactory
import org.qbrp.main.core.game.serialization.GameMapper

import org.qbrp.main.core.game.serialization.Serializer
import org.qbrp.main.core.mc.McObject
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil

class PlayerSerializer(val accounts: AccountDatabaseService,
                       val playerPrefab: RuntimePrefab.Tag): ContextualFactory<PlayerObject, String> {
    override fun fromJson(json: String, nickname: String): PlayerObject {
        val jsonField = GameMapper.COMPONENTS_JSON.decodeFromString<PlayerJsonField>(json)
        val entity = PlayersUtil.getPlayer(nickname)
            ?: throw NullPointerException("Игрок с ником ${jsonField.name} не найден на сервере")
        jsonField.name = nickname
        val account = runBlocking { accounts.get(jsonField.accountUuid) }
        return PlayerObject(entity, account!!, jsonField.state,)
    }

    override fun toJson(plr: PlayerObject): String {
        return GameMapper.COMPONENTS_JSON.encodeToString(
            PlayerJsonField(plr.account.uuid, plr.state, plr.name)
        )
    }

    suspend fun newInstance(player: ServerPlayerEntity, accountUuid: String): PlayerObject {
        val playerObject = PlayerObject(player, accounts.get(accountUuid)!!)
        playerPrefab.put(playerObject)
        return playerObject
    }
}