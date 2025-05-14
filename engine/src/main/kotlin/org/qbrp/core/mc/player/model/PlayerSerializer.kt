package org.qbrp.core.mc.player.model

import kotlinx.coroutines.runBlocking
import net.minecraft.server.network.ServerPlayerEntity

import org.qbrp.core.game.serialization.SerializeFabric
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.player.PlayerObject

class PlayerSerializer(val accounts: AccountDatabaseService): SerializeFabric<PlayerObject, PlayerJsonField>() {

    override fun toJson(plr: PlayerObject): PlayerJsonField {
        return PlayerJsonField(plr.id, plr.account.uuid, plr.state, plr.name)
    }

    override fun fromJson(json: PlayerJsonField): PlayerObject {
        val entity = PlayerManager.getPlayer(json.name) ?: throw NullPointerException("Игрок с ником ${json.name} не найден на сервере")
        val account = runBlocking { accounts.get(json.accountUuid) }
        return PlayerObject(entity, account!!, json.state)
    }

    suspend fun newInstance(player: ServerPlayerEntity, accountUuid: String): PlayerObject {
        return PlayerObject(player, accounts.get(accountUuid)!!)
    }
}