package org.qbrp.core.mc.player.model

import com.mongodb.client.model.Updates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.server.network.ServerPlayerEntity
import org.bson.conversions.Bson
import org.koin.core.component.KoinComponent
import org.qbrp.core.game.lifecycle.LifecycleManager
import org.qbrp.core.game.serialization.SerializeFabric
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.player.PlayerObject
import org.qbrp.core.mc.player.registration.LoginResult
import org.qbrp.core.mc.player.registration.PlayerRegistrationCallback
import org.qbrp.engine.Engine
import org.qbrp.engine.game.GameAPI
import org.qbrp.system.utils.format.Format.asMiniMessage

class PlayerLifecycleManager(
    override val storage: PlayerStorage,
    override val db: PlayerDatabaseService,
    override val fabric: PlayerSerializer
) : LifecycleManager<PlayerObject>(storage, db, fabric), KoinComponent {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val accounts = PlayerManager.accountDatabase

    override fun save(obj: PlayerObject) {
        super.save(obj)
        val updates = (obj.state.behaviours as List<PlayerBehaviour>)
            .flatMap { it.onAccountSave(obj.account, accounts.db!!) } +
                Updates.set("minecraftNicknames", obj.account.minecraftNicknames)
        accounts.saveAccountGameState(obj.account, updates)
    }


    fun handleConnected(player: ServerPlayerEntity, auth: String) {
        scope.launch {
            try {
                val result = accounts.login(player.name.string, auth)
                when (result) {
                    LoginResult.SUCCESS -> {
                        val prefab = Engine.getAPI<GameAPI>()!!.getPlayerPrefab()
                        val jsonField = db.getByField("accountUuid", auth, PlayerJsonField::class.java)
                        val playerObject = if (jsonField != null) {
                            jsonField.upsertForPlayer(player)
                            fabric.fromJson(jsonField)
                        } else {
                            fabric.newInstance(player, auth)
                        }
                        prefab.put(playerObject)
                        player.server.execute() {
                            onCreated(playerObject)
                            PlayerRegistrationCallback.EVENT.invoker().onRegister(playerObject, PlayerManager)
                        }
                    }
                    LoginResult.NOT_FOUND -> {
                        player.server.execute {
                            player.networkHandler.disconnect(
                                "<red>Аккаунт не найден.".asMiniMessage()
                            )
                        }
                    }

                    LoginResult.ALREADY_LINKED -> {
                        player.server.execute {
                            player.networkHandler.disconnect(
                                "<red>Имя уже привязано к другому аккаунту.".asMiniMessage()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                player.server.execute {
                    player.networkHandler.disconnect(
                        "<red>Ошибка при входе: ${e.message}".asMiniMessage()
                    )
                }
            }

        }
    }
    fun handleDisconnected(player: ServerPlayerEntity) {
        try {
            val obj = storage.getByPlayer(player)
            unload(obj)
        } catch (e: Exception) {
            if (e !is NullPointerException) {
                e.printStackTrace()
            }
        }
    }
}