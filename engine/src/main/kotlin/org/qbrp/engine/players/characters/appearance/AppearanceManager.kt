package org.qbrp.engine.players.characters.appearance

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.koin.core.component.KoinComponent
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.keybinds.ServerKeybindCallback
import org.qbrp.core.keybinds.ServerKeybinds.registerKeybindReceiver
import org.qbrp.engine.players.characters.Character
import org.qbrp.system.utils.format.Format.asMiniMessage

class AppearanceManager: KoinComponent {
    fun registerKeybindHandler() {
        registerKeybindReceiver("information")
        ServerKeybindCallback.getOrCreateEvent("information").register { player ->
            PlayerManager.getPlayerLookingAt(player)?.let {
                if (it.interactionManager.gameMode != GameMode.SPECTATOR) {
                    val session = PlayerManager.getPlayerSession(it)
                    val appearance = session.state.getComponent<Appearance>()
                    val character = session.state.getComponent<Character>()?.data
                    if (character != null && appearance != null && appearance.tooltip != "") {
                        sendAppearanceDescription(
                            session.displayName,
                            appearance.tooltip,
                            player
                        )
                        if (appearance.look != character.appearance.defaultLook
                            && character.appearance.look.description != null) {

                            sendAppearanceDescription(
                                session.displayName,
                                appearance.tooltip,
                                player
                            )
                        }
                        PlayerManager.getPlayerSession(player)
                            .state.getComponent<AppearanceNotifications>()?.read(character)
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