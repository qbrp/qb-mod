package org.qbrp.main.engine.players.characters.appearance

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.keybinds.ServerKeybindCallback
import org.qbrp.main.core.keybinds.ServerKeybindsAPI
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.players.characters.Character
import org.qbrp.main.engine.players.characters.acquaintances.NamesPerception
import org.qbrp.main.core.utils.format.Format.asMiniMessage

//TODO: Сделать компонентом
class AppearanceManager: KoinComponent {
    fun registerKeybindHandler() {
        get<ServerKeybindsAPI>().registerKeybindReceiver("information")

        ServerKeybindCallback.getOrCreateEvent("information").register { player ->
            val plrObject = PlayersUtil.getPlayerSession(player)
            PlayersUtil.getPlayerLookingAt(player)?.let {
                if (it.interactionManager.gameMode != GameMode.SPECTATOR) {
                    val session = PlayersUtil.getPlayerSession(it)
                    val appearance = session.state.getComponent<Appearance>()
                    val character = session.state.getComponent<Character>()
                    if (character != null && appearance != null && appearance.description != "") {
                        val displayName = plrObject.getComponent<NamesPerception>()?.getName(session) ?: session.displayName
                        val characterData = character.data
                        sendAppearanceDescription(
                            displayName,
                            appearance.description,
                            player
                        )
                        if (appearance.look != characterData.appearance.defaultLook
                            && appearance.look?.description != null) {

                            sendAppearanceDescription(
                                displayName,
                                appearance.look!!.description!!,
                                player
                            )
                        }
                        PlayersUtil.getPlayerSession(player).
                            state.getComponent<AppearanceNotificationsModule.AppearanceNotification>()?.read(session)
                    }
                }
            }
            ActionResult.PASS
        }
    }

    fun sendAppearanceDescription(name: String, text: String, player: ServerPlayerEntity) {
        player.sendMessage(
            "$name &d( &r$text&d )&r"
                .asMiniMessage())
    }

}