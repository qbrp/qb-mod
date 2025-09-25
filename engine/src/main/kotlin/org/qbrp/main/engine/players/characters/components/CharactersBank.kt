package org.qbrp.main.engine.players.characters.components

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.copy
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.serialization.SaveListener
import org.qbrp.main.core.components.state.ComponentHolder
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.storage.Table
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.accounts.AccountUpdate
import org.qbrp.main.engine.players.characters.entity.CharacterFactory
import org.qbrp.main.engine.players.characters.entity.CharacterFactory.Companion.createProfile
import org.qbrp.main.engine.players.characters.entity.CharacterFactory.Companion.upsertProfile
import org.qbrp.main.engine.players.characters.entity.CharacterProfile
import org.qbrp.main.engine.players.characters.entity.CharacterProfileDTO
import org.qbrp.main.engine.players.characters.events.CharacterApplyEvent
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext
import org.qbrp.main.mechanics.characters.appearance.looks.LookManager
import org.qbrp.main.mechanics.characters.model.CharacterData

class CharactersBank(
    ctx: PlayerComponentInstantiateContext,
    private val characters: MutableList<CharacterProfile> = mutableListOf()
) : PlayerComponent(ctx), SaveListener {

    @Serializable
    data class Data(val characters: List<CharacterProfileDTO>): DTO
    override val dto: DTO
        get() {
            saveCharacter()
            return Data(characters.toDtoAll())
        }

    companion object {
        val LOGGER = LoggerUtil.get("players", "characters")
    }

    var character: CharacterProfile? = null
        private set
    val characterData: CharacterData? get() = character?.data

    override fun onAccountSave(account: Account, db: Table): AccountUpdate {
        return if (character != null) {
            AccountUpdate(
                updates = listOf(
                    Updates.set("appliedCharacterName", characterData?.name),
                    Updates.set("characters.$[elem].appliedLookName", require<LookManager>().look.name)
                ),
                arrayFilters = listOf(
                    Filters.eq("elem.label", account.appliedCharacterName)
                )
            )
        } else {
            AccountUpdate(emptyList())
        }
    }

    val ctx: CharacterComponentInstantiateContext get() = newCtx(characterData!!)
    fun newCtx(characterData: CharacterData) = CharacterComponentInstantiateContext(serverPlayer, characterData)

    context(event: EventContext)
    fun setCharacterOrBuild(character: CharacterData, prefab: CharacterFactory) {
        try {
            val profile = characters.find { it.id == character.id } ?: createProfile(prefab, character)
            this.character = profile
            loadCharacter(profile, prefab)
            callUpserts(character)
            saveCharacter()
            CharacterApplyEvent(player, character).emit()
            sendMessage("<gray>Применён персонаж ${character.formattedName}")
        } catch (e: Exception) {
            sendMessage("<red>Возникла ошибка применения персонажа: ${e.message}. Свяжитесь с администратором.")
            e.printStackTrace()
        }
    }

    private fun loadCharacter(character: CharacterProfile, prefab: CharacterFactory) {
        unloadCharacter()
        upsertProfile(character, prefab, ctx)
        character.state.copy(player)
        forEach { if (it is CharacterBind && it is Activateable) it.enable() }
    }

    fun unloadCharacter() {
        if (character == null) return
        saveCharacter()
        forEach {
            if (it is CharacterBind) {
                if (it is Activateable) it.disable()
                removeComponent(it)
            }
        }
    }

    fun saveCharacter() {
        if (character == null) return
        updateCharacter(moveComponentsToProfile())
    }

    private fun callUpserts(characterData: CharacterData) {
        filterIsInstance<CharacterComponent>().forEach {
            it.upsert(characterData)
        }
    }

    private fun moveComponentsToProfile(): CharacterProfile {
        if (character == null) throw IllegalArgumentException("Character cannot be null")
        val copyState = state.copy(ComponentHolder()) { name, component ->
            component is CharacterBind
        }
        return CharacterProfile(serverPlayer, character!!.id, character!!.data, copyState)
    }

    private fun updateCharacter(character: CharacterProfile) {
        characters.remove(character)
        characters.add(character)
    }

    override fun onSave(state: ComponentManager) {
        state
            .namedIterator()
            .forEach { (name, component) ->
                if (component is CharacterBind) state.removeComponent(name.full)
            }
    }
}