package org.qbrp.main.engine.players.model.service

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.extensions.toDTO
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.mapping.PlayerJsonField

class PlayerSaver(private val serializer: PlayerSerializer, private val table: TableAccess): Saver<ServerPlayerObject>, KoinComponent {
    override fun saveObject(obj: ServerPlayerObject) {
        obj.apply {
//            val updates = filterIsInstance<PlayerComponent>()
//                .map {
//                    it.onAccountSave(account, accounts)
//                } + AccountUpdate(listOf(Updates.set("minecraftNicknames", account.minecraftNicknames)))
            val json = serializer.toJson(PlayerJsonField(id, state.toDTO(), username.value))
            table.saveObject(id, json)
//            accounts.saveAccountGameState(account, updates)
        }
    }
}
