package org.qbrp.engine.characters

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.ServerPlayerSession
import org.qbrp.core.keybinds.ServerKeybindCallback
import org.qbrp.core.keybinds.ServerKeybinds.registerKeybindReceiver
import org.qbrp.engine.characters.model.Appearance
import org.qbrp.system.utils.format.Format.asMiniMessage
import org.qbrp.system.utils.world.getPlayersInRadius
import kotlin.concurrent.fixedRateTimer

class AppearanceManager: KoinComponent {
    data class ReadState(val data: String, val time: Long)
    private val readStates = mutableMapOf<ServerPlayerSession, ReadState>()

    companion object {
        const val WAIT_TIME = 300 * 1000
    }

    fun registerKeybindHandler() {
        registerKeybindReceiver("information")
        ServerKeybindCallback.getOrCreateEvent("information").register { player ->
            val session = PlayerManager.getPlayerSession(player)
            PlayerManager.getPlayerLookingAt(player)?.let {
                if (it.interactionManager.gameMode != GameMode.SPECTATOR) {
                    val account = PlayerManager.getPlayerSession(it).account
                    val character = account!!.appliedCharacter
                    if (character != null) {
                        sendAppearanceDescription(
                            character.formattedName,
                            character.appearance.description,
                            session.entity
                        )
                        if (character.appearance.look != character.appearance.defaultLook && character.appearance.look.description != null) {
                            sendAppearanceDescription(
                                character.formattedName,
                                character.appearance.appliedLook?.description!!,
                                session.entity
                            )
                        }
                        session.account!!.social.readAppearance(account)
                    }
                }
            }
            ActionResult.PASS
        }
    }

    fun registerAppearanceReadTimer() {
        fixedRateTimer(
            name = "[qbrp/Characters] [ReadAppearanceTimer]",
            initialDelay = 0,
            period = 5000,
            daemon = true
        ) {
            // Фильтруем авторизованных игроков один раз
            PlayerManager.playersList.filter { it.isAuthorized() }.forEach { player ->
                // Получаем список соседей с описанным персонажем, которых игрок ещё не просмотрел
                val notRead = PlayerManager.playersList
                .filter { it.isAuthorized()
                        && it.entity != player.entity
                        && it.entity.interactionManager.gameMode != GameMode.SPECTATOR
                        && player.entity.canSee(it.entity) }
                // Авторизован ли игрок, не спектатор ли он
                .map { it.entity }
                .getPlayersInRadius(player.entity, 8.0)
                .map { PlayerManager.getPlayerSession(it) }
                // Получаем сессии игроков в радиусе
                .filter { it.account?.appliedCharacter != null
                        && !player.account!!.social.isAppearanceRead(it.account!!)
                }
                .filter { account ->
                    val state = readStates[account] ?: return@filter true
                    val appearance = account.account!!.appliedCharacter!!.appearance
                    val description = appearance.look.description

                    if (description == null) return@filter false

                    val composed = appearance.composeDescription()
                    if (state.data == composed) {
                        System.currentTimeMillis() > state.time + WAIT_TIME
                    } else {
                        true
                    }
                }
                if (notRead.isNotEmpty()) {
                    player.entity.sendMessage(
                        "<red>⬥ <gold>Вы не прочитали описание внешности ${
                            notRead.joinToString("&6, ") { it.displayName }
                        }".asMiniMessage()
                    )
                    notRead.forEach {
                        readStates[it] = ReadState(it.account!!.appliedCharacter!!.appearance.composeDescription(), System.currentTimeMillis())
                    }
                }
            }
        }
    }


    fun sendAppearanceDescription(name: String, text: String, player: ServerPlayerEntity) {
        player.sendMessage(
            "$name &d( &7$text&d )&r"
                .asMiniMessage())
    }

}