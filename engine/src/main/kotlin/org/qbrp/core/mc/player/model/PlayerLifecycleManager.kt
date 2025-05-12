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
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.utils.format.Format.asMiniMessage
import kotlin.concurrent.fixedRateTimer

class PlayerLifecycleManager(
    override val storage: PlayerStorage,
    override val db: PlayerDatabaseService,
    override val fabric: PlayerSerializer
) : LifecycleManager<PlayerObject>(storage, db, fabric), KoinComponent {

    private val accounts = PlayerManager.accountDatabase

    init {
        startSaveTimer()
    }

    override fun onCreated(obj: PlayerObject) {
        super.onCreated(obj)
        PlayerRegistrationCallback.EVENT.invoker().onRegister(obj, PlayerManager)
        obj.sendNetworkMessage(Message(Messages.AUTH, Signal()))
    }

    fun startSaveTimer() {
        fixedRateTimer("qbrp/SavePlayerObjTimer", true, 0L, 1000L * 60L) {
            storage.getAll().forEach {
                save(it)
            }
        }
    }

    override fun save(obj: PlayerObject) {
        super.save(obj)
        val updates = (obj.state.behaviours as List<PlayerBehaviour>)
            .map {
                it.onAccountSave(obj.account, accounts.db!!)
            } + AccountUpdate(listOf(Updates.set("minecraftNicknames", obj.account.minecraftNicknames)))
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