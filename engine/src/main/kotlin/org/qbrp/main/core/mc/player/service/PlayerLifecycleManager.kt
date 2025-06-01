package org.qbrp.main.core.mc.player.service

import com.mongodb.client.model.Updates
import kotlinx.coroutines.launch
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.lifecycle.LifecycleManager
import org.qbrp.main.core.mc.McObject
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.PlayersAPI
import org.qbrp.main.core.mc.player.registration.LoginResult
import org.qbrp.main.core.mc.player.registration.PlayerRegistrationCallback
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.Signal
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import kotlin.concurrent.fixedRateTimer

class PlayerLifecycleManager(
    override val storage: PlayerStorage,
    override val table: TableAccess,
    override val fabric: PlayerSerializer,
    private val accounts: AccountDatabaseService,
    private val api: PlayersAPI
) : LifecycleManager<PlayerObject>(storage, table, fabric), KoinComponent {

    init {
        startSaveTimer()
    }

    override fun onCreated(obj: PlayerObject) {
        super.onCreated(obj)
        PlayerRegistrationCallback.EVENT.invoker().onRegister(obj, api)
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
                it.onAccountSave(obj.account, accounts)
            } + AccountUpdate(listOf(Updates.set("minecraftNicknames", obj.account.minecraftNicknames)))
        accounts.saveAccountGameState(obj.account, updates)
    }

    fun handleAuth(player: ServerPlayerEntity, auth: String) {
        scope.launch {
            try {
                val result = accounts.login(player.name.string, auth)
                when (result) {
                    LoginResult.SUCCESS -> {
                        val m = this@PlayerLifecycleManager as Lifecycle<McObject>
                        val document = table.getByField("accountUuid", auth)
                        val json = document.await()?.toJson()
                        val playerObject =
                            if (json != null) fabric.fromJson(json, player.name.string, m)
                            else fabric.newInstance(player, auth, m)
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