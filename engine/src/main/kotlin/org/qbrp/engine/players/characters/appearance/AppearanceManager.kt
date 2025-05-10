package org.qbrp.engine.players.characters.appearance

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.keybinds.ServerKeybindCallback
import org.qbrp.core.keybinds.ServerKeybinds.registerKeybindReceiver
import org.qbrp.engine.players.characters.Character
import org.qbrp.engine.players.characters.acquaintances.NamesPerception
import org.qbrp.system.utils.format.Format.asMiniMessage

class AppearanceManager: KoinComponent {
    fun registerKeybindHandler() {
        registerKeybindReceiver("information")
        ServerKeybindCallback.getOrCreateEvent("information").register { player ->
            val plrObject = PlayerManager.getPlayerSession(player)
            PlayerManager.getPlayerLookingAt(player)?.let {
                if (it.interactionManager.gameMode != GameMode.SPECTATOR) {
                    val session = PlayerManager.getPlayerSession(it)
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
                        PlayerManager.getPlayerSession(player).
                            state.getComponent<AppearanceNotificationsModule.AppearanceNotifications>()?.read(session)
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