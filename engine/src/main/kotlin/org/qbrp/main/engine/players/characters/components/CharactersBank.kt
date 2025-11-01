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
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.storage.Table
import org.qbrp.main.core.util.log.LoggerUtil
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.accounts.AccountUpdate
import org.qbrp.main.engine.players.characters.CharacterRecord
import org.qbrp.main.engine.players.characters.CharacterRecordDto
import org.qbrp.main.engine.players.characters.createCharacterProfile
import org.qbrp.main.engine.players.characters.CharacterApplyEvent
import org.qbrp.main.engine.players.characters.upsertCharacter
import org.qbrp.main.engine.players.model.PlayerComponent
import org.qbrp.main.engine.players.model.PlayerComponentInstantiateContext
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.engine.players.characters.loadCharacterComponents
import org.qbrp.main.engine.players.characters.toDtoAll
import org.qbrp.main.engine.players.customName
import org.qbrp.main.engine.players.removeCustomName

class CharactersBank(
    ctx: PlayerComponentInstantiateContext,
    private val characters: MutableList<CharacterRecord> = mutableListOf()
) : PlayerComponent(ctx), SaveListener {

    @Serializable
    data class Data(val characters: List<CharacterRecordDto>): DTO
    override val dto: DTO
        get() {
            saveCharacter()
            return Data(characters.toDtoAll())
        }

    var character: CharacterRecord? = null
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

    override fun onLeave() {
        unloadCharacter()
    }

    val ctx: CharacterComponentInstantiateContext get() = newCtx(characterData!!)
    fun newCtx(characterData: CharacterData) = CharacterComponentInstantiateContext(serverPlayer, characterData)

    fun setCharacterOrBuild(character: CharacterData) {
        if (character.id == this.character?.id) return
        try {
            val profile = characters.find { it.id == character.id } ?: createCharacterProfile(newCtx(character))
            loadCharacter(profile)
            callUpserts(character)
            saveCharacter()
            CharacterApplyEvent(player, character).emit()
            sendMessage("<gray>Применён персонаж ${character.formattedName}")
        } catch (e: Exception) {
            sendMessage("<red>Возникла ошибка применения персонажа: ${e.message}. Свяжитесь с администратором.")
            e.printStackTrace()
        }
    }

    private fun loadCharacter(character: CharacterRecord) {
        unloadCharacter()
        this.character = character
        character.state.copy(player)
        loadCharacterComponents(ctx)
        upsertCharacter(character, ctx)
        forEach { if (it is CharacterBind && it is Activateable) it.enable() }
    }

    fun unloadCharacter() {
        val character = character
        if (character == null) return
        saveCharacter()
        forEach {
            if (it is CharacterBind) {
                if (it is Activateable) it.disable()
                removeComponent(it)
            }
        }
        if (character.data.formattedName == player.customName) player.removeCustomName()
        this.character = null
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

    private fun moveComponentsToProfile(): CharacterRecord {
        if (character == null) throw IllegalArgumentException("PhysicalCharacter cannot be null")
        val copyState = state.copy(ComponentHolder()) { name, component ->
            component is CharacterBind
        }
        return CharacterRecord(serverPlayer, character!!.id, character!!.data, copyState)
    }

    private fun updateCharacter(character: CharacterRecord) {
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

    companion object {
        val LOGGER = LoggerUtil.get("players", "characters")
    }
}