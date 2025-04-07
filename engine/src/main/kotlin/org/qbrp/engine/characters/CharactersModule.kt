package org.qbrp.engine.characters

import klite.Session
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.ServerPlayerSession
import org.qbrp.core.game.player.registration.PlayerRegistrationCallback
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.keybinds.ServerKeybindCallback
import org.qbrp.core.keybinds.ServerKeybinds.registerKeybindReceiver
import org.qbrp.engine.Engine
import org.qbrp.engine.characters.model.Appearance
import org.qbrp.engine.characters.model.Character
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule
import org.qbrp.system.utils.format.Format.asMiniMessage

@Autoload(9)
class CharactersModule: QbModule("characters") {
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun load() {
        PlayerRegistrationCallback.EVENT.register { session, manager ->
            if (!session.account!!.characters.isEmpty()) {
                applyCharacter(session)
            }
        }
        CommandsRepository.add(listOf(get<ApplyCharacterCommand>(), get<ApplyLookCommand>()))
        registerKeybindReceiver("information")
        ServerKeybindCallback.getOrCreateEvent("information").register { player ->
            println("Просмотр информации")
            val session = PlayerManager.getPlayerSession(player)
            PlayerManager.getPlayerLookingAt(player)?.let {
                val character = PlayerManager.getPlayerSession(it).account?.appliedCharacter
                if (character != null) {
                    session.entity.sendMessage(
                        "${character.formattedName}<newline>&d( &7${character.appearance.composeDescription()}&d )&r"
                            .asMiniMessage())
                }
            }
            ActionResult.PASS
        }
    }

    override fun getKoinModule() = module {
        single { ApplyCharacterCommand(this@CharactersModule) }
        single { ApplyLookCommand(this@CharactersModule) }
    }

    fun getCharacter(session: ServerPlayerSession): Character {
        return session.account!!.appliedCharacter
            ?: throw NullPointerException("No character found: ${session.account!!.appliedCharacterName}")

    }

    fun applyCharacter(session: ServerPlayerSession, name: String = session.account!!.appliedCharacter!!.name) {
        val chatAPI = Engine.getAPI<ChatAPI>()!!
        try {
            session.account!!.appliedCharacterName = name
            val character = getCharacter(session)
            session.entity.server.commandManager.executeWithPrefix(
                session.entity.server.commandSource, "scale set pehkui:base ${character.scaleFactor} ${session.entity.name.string}"
            )
            applyLook(session)
            chatAPI.sendMessage(session.entity, "<gray>Применён персонаж ${character.formattedName}")
        } catch (e: NullPointerException) {
            chatAPI.sendMessage(session.entity, "<gray>Персонаж не найден.")
        }
    }

    fun applyLook(session: ServerPlayerSession, look: Appearance.Look = session.account!!.appliedCharacter!!.appearance.look) {
        val chatAPI = Engine.getAPI<ChatAPI>()!!
        try {
            val character = getCharacter(session)
            session.account!!.appliedCharacter!!.appearance.appliedLook = look
            val look = character.appearance.look
            if (look.skinUrl != null) {
                session.executeCommand("""skin url "${character.appearance.skinUrl}" ${character.appearance.model}""")
            }
            chatAPI.sendMessage(
                session.entity,
                "<gray>Применён облик ${character.appearance.look.getColoredName(character)}"
            )
        } catch (e: NullPointerException) {
            chatAPI.sendMessage(session.entity, "<gray>Персонаж не найден.")
        }
    }
}